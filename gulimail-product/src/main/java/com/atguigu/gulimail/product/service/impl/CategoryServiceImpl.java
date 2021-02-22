package com.atguigu.gulimail.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimail.product.service.CategoryBrandRelationService;
import com.atguigu.gulimail.product.vo.ResponseCategoryLog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.CategoryDao;
import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> treeStructure() {
        //查出全部分类
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //找出1级分类
        List<CategoryEntity> collect = selectList.stream().
                filter(each -> each.getParentCid() == 0)
                .map(each -> {
                    //找出全部子分类 并赋值
                    each.setSubcategoryList(getStructure(each, selectList));
                    return each;
                }).sorted((each1, each2) -> (each1.getSort() == null ? 0 : each1.getSort()) - (each2.getSort() == null ? 0 : each2.getSort())
                ).collect(Collectors.toList());
        //找出1级分类下面的子分类

        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        //TODO 1检查当前删除的菜单是否被别的地方引用
        baseMapper.deleteBatchIds(catIds);
    }

    @Override
    public Long[] getCatelogPath(Long catelogId) {
        List<Long> lists = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, lists);
        Collections.reverse(parentPath);
        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }

    @Override
    @Transactional
    public void updateCategoryEntityById(CategoryEntity category) {
        //update this
        this.updateById(category);
        //update categoryBrandRelation  name
        categoryBrandRelationService.updateCategoryBycategoryId(category.getCatId(), category.getName());
        //TODO

    }

    private List<Long> findParentPath(Long categoryId, List<Long> list) {
        list.add(categoryId);
        CategoryEntity byId = this.getById(categoryId);
        if (byId != null && byId.getParentCid() != null && byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), list);
        }
        return list;
    }

    private List<CategoryEntity> getStructure(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream().filter(each -> {
            return each.getParentCid().equals(root.getCatId());
        }).map(each -> {
            //找出子分类下面的子分类 --递归查找
            each.setSubcategoryList(getStructure(each, all));
            return each;
        }).sorted((each1, each2) -> {
            return (each1.getSort() == null ? 0 : each1.getSort()) - (each2.getSort() == null ? 0 : each2.getSort());
        }).collect(Collectors.toList());
    }


    @Override
    public List<CategoryEntity> getLevelCategorys() {
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Override
    public Map<String, List<ResponseCategoryLog2Vo>> getCatelogJson() {
        //缓存穿透 空结果返回
        //缓存雪崩 设置过期时间 +随机值
        //缓存击穿  加lock锁
        String categoryLogList = stringRedisTemplate.opsForValue().get("categoryLogJson");
        if (StringUtils.isEmpty(categoryLogList)) {
            log.info("缓存没命中-------查询数据库-----");
            //从数据库查找
            return getCatelogJsonFromRedisLockDB();
        }
        log.info("缓存命中-------查询redis缓存-----");
        //反序列化
        return JSON.parseObject(categoryLogList, new TypeReference<Map<String, List<ResponseCategoryLog2Vo>>>() {
        });
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> categoryEntityLists, Long parent_cid) {
        return categoryEntityLists.stream().filter(item -> {
            return item.getParentCid() == parent_cid;
        }).collect(Collectors.toList());
    }

    /**
     * @return 从数据库查询并封装分类数据
     */
    private synchronized Map<String, List<ResponseCategoryLog2Vo>> getCatelogJsonFromDB() {
        return getFromDB();
    }


    /**
     * @return 从数据库查询并封装分类数据
     */
    private Map<String, List<ResponseCategoryLog2Vo>> getCatelogJsonFromRedisLockDB() {

        //设置 过期时间和 获取锁 成原子操作
        String uuid = UUID.randomUUID().toString();
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (flag) {
            Map<String, List<ResponseCategoryLog2Vo>> fromDB = null;
            log.info("加锁成功ok---");
            try {
                //成功了 执行业务
                fromDB = getFromDB();
            } finally {
                log.info("解锁成功ok---");
                //删除锁 删除之前必须查找到当前的锁是否存在 查找和删除必须是原子操作 参考redis 官方文档
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                //调用脚本
                stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return fromDB;
        } else {
            log.info("加锁失败,正在重试再次加锁---");
            //加锁失败 等待
            //休眠100毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJsonFromRedisLockDB();
        }
    }

    private Map<String, List<ResponseCategoryLog2Vo>> getFromDB() {
        String categoryLogList = stringRedisTemplate.opsForValue().get("categoryLogJson");
        if (!StringUtils.isEmpty(categoryLogList)) {
            //直接返回结果
            return JSON.parseObject(categoryLogList, new TypeReference<Map<String, List<ResponseCategoryLog2Vo>>>() {
            });
        }
        log.info("查询了数据库DB------------------");

        //拿到全部分类 --通过一次查出全部的分类 减少堆数据库访问次数
        List<CategoryEntity> categoryEntityLists = this.list();

        //1查出所有一级分类
        List<CategoryEntity> categorys = getParent_cid(categoryEntityLists, 0L);
        //2封装数据
        Map<String, List<ResponseCategoryLog2Vo>> listMap = categorys.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();
        }, v -> {
            List<ResponseCategoryLog2Vo> collect = null;
            //拿到1级分类下面的所有的2分类
            List<CategoryEntity> categoryEntities = getParent_cid(categoryEntityLists, v.getCatId());
            if (!categoryEntities.isEmpty()) {
                collect = categoryEntities.stream().map(item -> {
                    ResponseCategoryLog2Vo categoryLog2Vo1 = new ResponseCategoryLog2Vo();
                    categoryLog2Vo1.setCatalog1Id(v.getCatId().toString());
                    categoryLog2Vo1.setName(item.getName());
                    categoryLog2Vo1.setId(item.getCatId().toString());
                    //拿到2级分类下面的所有的3分类
                    List<ResponseCategoryLog2Vo.CategoryLog3Vo> log3Vos = null;
                    List<CategoryEntity> categoryEntityList = getParent_cid(categoryEntityLists, item.getCatId());
                    if (!categoryEntityList.isEmpty()) {
                        log3Vos = categoryEntityList.stream().map(each -> {
                            ResponseCategoryLog2Vo.CategoryLog3Vo log3Vo = new ResponseCategoryLog2Vo.CategoryLog3Vo();
                            log3Vo.setId(each.getCatId().toString());
                            log3Vo.setName(each.getName());
                            log3Vo.setCatalog2Id(item.getCatId().toString());
                            return log3Vo;
                        }).collect(Collectors.toList());
                    }
                    categoryLog2Vo1.setCatalog3List(log3Vos);
                    return categoryLog2Vo1;
                }).collect(Collectors.toList());
            }
            return collect;
        }));
        //将结果放入 redis缓存
        stringRedisTemplate.opsForValue().set("categoryLogJson", JSON.toJSONString(listMap), 1, TimeUnit.DAYS);
        return listMap;
    }
}