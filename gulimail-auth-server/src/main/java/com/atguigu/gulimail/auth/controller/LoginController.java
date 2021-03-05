package com.atguigu.gulimail.auth.controller;


import com.atguigu.common.utils.R;
import com.atguigu.gulimail.auth.feign.SmsSendFeignService;
import com.atguigu.gulimail.auth.feign.UserFeignService;
import com.atguigu.gulimail.auth.vo.UserLoginVo;
import com.atguigu.gulimail.auth.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.AuthSmsServerConstant.SMS_CODE_REDIS_CACHE_PREFIX;
import static com.atguigu.common.exception.BizCodeEnume.VAILD_SMS_CODE_Exception;

/**
 * 如果只是发送一个请求直接跳转一个页面 springmvc viewcontroller 将请求和页面直接映射 --参考 MyWebConfig
 */
@Slf4j
@Controller
public class LoginController {


    @Autowired
    private SmsSendFeignService smsSendFeignService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserFeignService userFeignService;

    /**
     * 1接口防止重复刷
     * 2验证码再次校验 redis
     *
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/sms/sendcode")
    public R sendSmsCode(@RequestParam("phone") String phone) {
        //todo 接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(SMS_CODE_REDIS_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long parseLong = Long.parseLong(redisCode.split("_")[1]);
            //判断是否在 60s之内
            if (System.currentTimeMillis() - parseLong < 60000) {
                return R.error(VAILD_SMS_CODE_Exception.getCode(), VAILD_SMS_CODE_Exception.getMessage());
            }
        }
        String coode = UUID.randomUUID().toString().substring(0, 5);
        String redisCodeKey = coode + "_" + System.currentTimeMillis();
        //k: sms:code:17613720880
        //v: code
        stringRedisTemplate.opsForValue().set(SMS_CODE_REDIS_CACHE_PREFIX + phone, redisCodeKey, 10, TimeUnit.MINUTES);
        smsSendFeignService.sendCode(phone, coode);
        return R.ok();
    }


    /**
     * 注册用户逻辑编写
     *
     * @param userRegisterVo
     * @param result
     * @param redirectAttributes 重定向数据进行保存
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo userRegisterVo,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        //转发到注册页面
        if (result.hasErrors()) {
            Map<String, String> stringMap = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (entity1, entity2) -> entity1));
            //这些数据只需要取一次就行了
            redirectAttributes.addFlashAttribute("errors", stringMap);
            //Request method 'POST' not supported
            //return "forward:/register.html";
            //重定向 可以解决表单重复提交的问题,但是会造成 model 无法保存数据 ---可以使用RedirectAttributes 进行保存数据
            //重定向 携带数据利用session 原理,将数据放在session,只要跳到下一个页面取出这个数据以后,session这个数据就会删除
            //如果用到session 就会出现分布式session 问题
            return "redirect:http://auth.gulimail.com/register.html";
        }

        //校验验证码
        String code = stringRedisTemplate.opsForValue().get(SMS_CODE_REDIS_CACHE_PREFIX + userRegisterVo.getPhone());

        if (!StringUtils.isEmpty(code)) {
            String codeString = code.split("_")[0];
            if (userRegisterVo.getCode().equalsIgnoreCase(codeString)) {
                //删除验证码 令牌机制
                stringRedisTemplate.delete(SMS_CODE_REDIS_CACHE_PREFIX + userRegisterVo.getPhone());
                //登录逻辑
                R r = userFeignService.register(userRegisterVo);
                log.info("远程调用后台系统进行保存 ,结果为 {}", r.get("code"));
                Integer.parseInt(r.get("code").toString());
                if (null != r.get("code") && Integer.parseInt(r.get("code").toString()) == 0) {
                    //重定向到登录页面
                    return "redirect:http://auth.gulimail.com/login.html";
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("msg", r.get("msg").toString());
                    redirectAttributes.addFlashAttribute("errors", map);
                    return "redirect:http://auth.gulimail.com/register.html";
                }
            } else {
                //效验出错 --code
                Map<String, String> map = new HashMap<>();
                map.put("code", "验证码效验失败");
                redirectAttributes.addFlashAttribute("errors", map);
                return "redirect:http://auth.gulimail.com/register.html";
            }
        } else {
            //redis 查询手机号未查询到
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码为空,请重试");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimail.com/register.html";
        }
    }


    @PostMapping("/login")
    public String login(@Valid UserLoginVo userLoginVo,
                        BindingResult result,
                        RedirectAttributes redirectAttributes
                        ) {

        if (result.hasErrors()) {
            Map<String, String> map = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (entity1, entity2) -> entity1));
            redirectAttributes.addFlashAttribute("errors", map);
            return "redirect:http://auth.gulimail.com/login.html";
        }
        R login = userFeignService.login(userLoginVo);
        log.info("远程调用member服务进行登录,结果为{}", login.get("code"));
        if (Integer.parseInt(login.get("code").toString()) == 0) {
            //登录成功
            return "redirect:http://gulimail.com/";
        }
        //登录失败跳转 登录页面
        Map<String, String> map = new HashMap<>();
        map.put("msg", login.get("msg").toString());
        redirectAttributes.addFlashAttribute("errors", map);
        return "redirect:http://auth.gulimail.com/login.html";
    }

}
