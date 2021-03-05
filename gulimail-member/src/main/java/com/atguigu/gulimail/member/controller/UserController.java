package com.atguigu.gulimail.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.common.vo.GithubEntityVo;
import com.atguigu.gulimail.member.exception.PhoneExistException;
import com.atguigu.gulimail.member.exception.UserNameExistException;
import com.atguigu.gulimail.member.feign.CouponService;
import com.atguigu.gulimail.member.vo.UserLoginVo;
import com.atguigu.gulimail.member.vo.UserRegisterVo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimail.member.entity.UserEntity;
import com.atguigu.gulimail.member.service.UserService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

import static com.atguigu.common.exception.BizCodeEnume.*;


/**
 * 用户表
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:25:51
 */
@RestController
@RequestMapping("member/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;


    @RequestMapping(value = "/coupons")
    public R test() {
        UserEntity entity = new UserEntity();
        entity.setNickname("张三");
        R r = couponService.memberCoupon();

        return R.ok().put("user", entity).put("conpons", r);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:user:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = userService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:user:info")
    public R info(@PathVariable("id") Long id) {
        UserEntity user = userService.getById(id);

        return R.ok().put("user", user);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:user:save")
    public R save(@RequestBody UserEntity user) {
        userService.save(user);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:user:update")
    public R update(@RequestBody UserEntity user) {
        userService.updateById(user);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:user:delete")
    public R delete(@RequestBody Long[] ids) {
        userService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    @PostMapping("/user/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo) {
        try {
            userService.registerUser(userRegisterVo);
        } catch (UserNameExistException e) {
            return R.error(USER_EXIST_EXCEPTION.getCode(), USER_EXIST_EXCEPTION.getMessage());
        } catch (PhoneExistException e) {
            return R.error(PHONE_EXIST_EXCEPTION.getCode(), PHONE_EXIST_EXCEPTION.getMessage());
        } catch (Exception e) {
            return R.error(USER_REGISTER_CONTRARY_EXCEPTION.getCode(), USER_REGISTER_CONTRARY_EXCEPTION.getMessage());
        }
        return R.ok();
    }

    @PostMapping("/user/login")
    public R login(@RequestBody UserLoginVo userLoginVo) {
        UserEntity userEntity = userService.login(userLoginVo);
        if (null != userEntity) return R.ok();
        else return R.error(USER_LOGIN_EXIST_EXCEPTION.getCode(), USER_LOGIN_EXIST_EXCEPTION.getMessage());
    }


    @PostMapping("oauth/github")
    public R githubLogin(@RequestBody GithubEntityVo githubEntityVo) {
        //判断当前社交用户是否已经注册过
        UserEntity entity = userService.socialContactByOnlyId(githubEntityVo);
        if (null == entity) {
            //第一次登录需要注册,保存用户信息
            entity = userService.githubRegister(githubEntityVo);
        } else {
            //更新 accessToken
            entity = userService.updateUserEntityByOnlyId(githubEntityVo);
        }
        return R.ok().put("data", entity);
    }

}
