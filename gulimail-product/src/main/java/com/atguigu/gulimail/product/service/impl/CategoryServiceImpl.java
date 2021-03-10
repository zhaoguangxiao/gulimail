package com.atguigu.gulimail.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.vo.ResponseCategoryLog2Vo;
import com.atguigu.common.vo.ResponseThreeLeveVo;
import com.atguigu.gulimail.product.dao.CategoryDao;
import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.CategoryBrandRelationService;
import com.atguigu.gulimail.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private RedissonClient redissonClient;

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

    /**
     * 1缓存的所有数据都有过期时间 --触发主动更新
     * 2读写数据的时候+分布式读写锁 (经常写,经常读会有影响)
     * //删除多个缓存
     * Caching
     * CacheEvict(cacheNames = {"category"},allEntries = true) 删除这个分区下面全部数据
     *
     * @param category
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = {"category"}, key = "'getLevelCategorys'"),
            @CacheEvict(cacheNames = {"category"}, key = "'getCatelogJson'"),
            @CacheEvict(cacheNames = {"category"}, key = "'threeLeaveCategory'")
    })
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


    /**
     * Cacheable
     * 默认行为: 1),如果缓存中有,方法不用调用
     * 2),key 默认自动生成,缓存的名字 ::SimpleKey [] (自动生成的key)
     * 3), 缓存的 value 的值,默认使用 jdk 序列化机制,将序列化后的数据保存到redis
     * 4), 默认ttl 是-1(永不过期)
     * 自定义:
     * 1),指定缓存生成key
     * 2),指定缓存数据存活时间 (配置文件修改)
     * 3),将数据value 保存为json格式 (修改缓存管理器)
     *
     * @return
     */
    //每一个缓存的数据我们都需要来指定是放在哪个名字的缓存,[缓存的分区(按照业务员类型分区)]
    @Cacheable(cacheNames = {"category"}, key = "#root.methodName") //代表当前方法的结果是需要缓存的,如果缓存中存在数据,方法不再调用,否则先执行方法,在执行缓存
    @Override
    public List<CategoryEntity> getLevelCategorys() {
        log.info("getLevelCategorys 从数据库查询...");
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 解决缓存击穿 ---sync 加锁
     *
     * @return
     */
    @Cacheable(cacheNames = "category", key = "#root.method.name", sync = true)
    @Override
    public Map<String, List<ResponseCategoryLog2Vo>> getCatelogJson() {
        log.info("查询了数据库DB------------------");

        //拿到全部分类 --通过一次查出全部的分类 减少堆数据库访问次数
        List<CategoryEntity> categoryEntityLists = this.list();

        //1查出所有一级分类
        List<CategoryEntity> categorys = getParent_cid(categoryEntityLists, 0L);
        //2封装数据
        return categorys.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();
        }, v -> {
            List<ResponseCategoryLog2Vo> collect = getResponseCategoryLog2Vos(categoryEntityLists, v);
            return collect;
        }));
    }

    private List<ResponseCategoryLog2Vo> getResponseCategoryLog2Vos(List<CategoryEntity> categoryEntityLists, CategoryEntity v) {
        List<ResponseCategoryLog2Vo> collect = null;
        //拿到1级分类下面的所有的2分类
        List<CategoryEntity> categoryEntities = getParent_cid(categoryEntityLists, v.getCatId());
        if (!CollectionUtils.isEmpty(categoryEntities)) {
            collect = categoryEntities.stream().map(item -> {
                ResponseCategoryLog2Vo categoryLog2Vo1 = new ResponseCategoryLog2Vo();
                categoryLog2Vo1.setCatalog1Id(v.getCatId().toString());
                categoryLog2Vo1.setName(item.getName());
                categoryLog2Vo1.setId(item.getCatId().toString());
                //拿到2级分类下面的所有的3分类
                List<ResponseCategoryLog2Vo.CategoryLog3Vo> log3Vos = null;
                List<CategoryEntity> categoryEntityList = getParent_cid(categoryEntityLists, item.getCatId());
                if (!CollectionUtils.isEmpty(categoryEntityList)) {
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
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> categoryEntityLists, Long parent_cid) {
        return categoryEntityLists.stream().filter(item -> {
            return item.getParentCid() == parent_cid;
        }).collect(Collectors.toList());
    }

    /**
     * @return 从数据库查询并封装分类数据 1.0版本
     */
    private synchronized Map<String, List<ResponseCategoryLog2Vo>> getCatelogJsonFromDB() {
        return getFromDB();
    }


    /**
     * @return 从数据库查询并封装分类数据 2.0版本 使用set nx 原子性进行获取锁
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

    /**
     * @return 使用redisson 进行分布式锁 3.0版本
     */
    private Map<String, List<ResponseCategoryLog2Vo>> getCatelogJsonFromRedissonLockDB() {

        //锁的名字--锁的粒度,越细越好[具体缓存的是某个数据---如11好商品 product-11-lock]
        RLock lock = redissonClient.getLock("catelogJsonLock");
        //加锁
        lock.lock();
        Map<String, List<ResponseCategoryLog2Vo>> fromDB = null;
        try {
            log.info("分布式锁 获取成功,正在进行从数据库查询数据");
            fromDB = getFromDB();
        } finally {
            log.info("数据获取成功,分布式锁正在释放");
            lock.unlock();
        }
        return fromDB;
    }


    /**
     * 缓存里面的数据如何和数据库保持一致性
     * 1), 双写模式 (修改完数据库的数据,然后在修改redis 缓存的数据)
     * 缺点: 可能出现脏数据(由于卡顿的原因,导致写缓存2在最前,1在最后,就出现了数据不一致问题)
     * 解决: 1),加锁,当1号数据修改完,释放锁2号才能进行写入缓存进行业务操作
     * 2),给缓存数据加上一个过期时间,例如1天,到1天后,自动查询最新的数据加入缓存--暂时性的脏数据问题
     * 2), 失效模式 (修改完数据库的数据,将redis缓存删除)
     * 缺点: 也可能出现脏数据
     * 解决: 也可以用加锁来解决
     *
     * @return
     */
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
            List<ResponseCategoryLog2Vo> collect = getResponseCategoryLog2Vos(categoryEntityLists, v);
            return collect;
        }));
        //将结果放入 redis缓存
        stringRedisTemplate.opsForValue().set("categoryLogJson", JSON.toJSONString(listMap), 1, TimeUnit.DAYS);
        return listMap;
    }


    @Cacheable(cacheNames = "category",key = "#root.methodName", sync = true)
    @Override
    public List<ResponseThreeLeveVo> threeLeaveCategory() {

        //拿到全部分类 --通过一次查出全部的分类 减少堆数据库访问次数
        List<CategoryEntity> categoryEntityLists = this.list();

        //1查出所有一级分类
        List<CategoryEntity> categorys = getParent_cid(categoryEntityLists, 0L);

        return categorys.stream().map(item -> {
            ResponseThreeLeveVo responseThreeLeveVo = new ResponseThreeLeveVo();
            //设置一级分类name
            responseThreeLeveVo.setCatalog1Name(item.getName());
            //设置一级分类id
            responseThreeLeveVo.setCatalog1Id(item.getCatId().toString());
            //设置二级分类 三级分类
            List<ResponseCategoryLog2Vo> collect = getResponseCategoryLog2Vos(categoryEntityLists, item);
            responseThreeLeveVo.setResponseCategoryLog2VoList(collect);

            return responseThreeLeveVo;
        }).collect(Collectors.toList());

    }
}