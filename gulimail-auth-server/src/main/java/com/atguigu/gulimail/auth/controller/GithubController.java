package com.atguigu.gulimail.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimail.auth.properties.GitHubProperties;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.GithubEntityVo;
import com.atguigu.gulimail.auth.feign.UserFeignService;
import com.atguigu.common.vo.LoginUserVo;
import com.atguigu.gulimail.auth.properties.DomainNameProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.atguigu.gulimail.auth.properties.GitHubProperties.*;
import static com.atguigu.common.constant.LoginUserConstant.LOGIN_USER_KEY;


@Slf4j
@RequestMapping(value = "github")
@Controller
public class GithubController {


    @Autowired
    private GitHubProperties gitHubProperties;

    @Autowired
    private DomainNameProperties domainNameProperties;

    @Autowired
    private UserFeignService userFeignService;


    @GetMapping("callback")
    public String githubAuth(@RequestParam("code") String code,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {

        if (!StringUtils.isEmpty(code)) {
            //拿到我们的code,去请求token
            //发送一个请求到
            String token_url = GitHubProperties.TOKEN_URL.replace("CLIENTID", gitHubProperties.getClientId()).replace("CLIENTSECRET", gitHubProperties.getClientSecret()).replace("REDIRECTURI", gitHubProperties.getCallback()).replace("CODE", code);
            //得到的responseStr是一个字符串需要将它解析放到map中
            try {
                String responseStr = HttpUtils.doGet(token_url, null, null);
                String accessToken = parseResponseEntity(responseStr).get("access_token");
                //根据token发送请求获取登录人的信息  ，通过令牌去获得用户信息
                log.info("当前第三方登录的 accessToken={}", accessToken);
                String userResult = HttpUtils.doGet(ROOT_PATH_URL, GET_GITHUB_USER_HEADER_KEY, GET_GITHUB_USER_HEADER_VAL + accessToken);
                log.info("当前登录的用户数据是 {}", userResult);
                GithubEntityVo githubEntityVo = JSON.parseObject(userResult, new TypeReference<GithubEntityVo>() {
                });
                //如果第一次登录 需要注册进来(为当前社交用户生成一个会员信息账号,以后这个社交账号进来就对应指定的会员信息)
                githubEntityVo.setAccessToken(accessToken);
                //判断是否为第一次 如果是第一次就注册 否则就直接登录
                R login = userFeignService.githubLogin(githubEntityVo);
                log.info("调用用户后台进行保存第三方登录的github信息,{}", login.get("data"));

                //提取登录的用户信息
                LoginUserVo data = JSON.parseObject(JSON.toJSONString(login.get("data")), new TypeReference<LoginUserVo>() {
                });

                //session 保存
                //第一次使用session, 命令浏览器保存卡号 jessionid 这个cookie
                //以后浏览器访问那个网站都会带上这个网站的cookie
                //子域名之间, gulimail.com auth.gulimail.com item.gulimail.com
                //发卡的时候(指定域名的父域名),即使子域名系统发的卡,也能让父域名直接使用
                session.setAttribute(LOGIN_USER_KEY, data);

                return "redirect:" + domainNameProperties.getRootUrl();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //登录失败跳转 登录页面
            Map<String, String> map = new HashMap<>();
            map.put("msg", "第三方登录出现了意料之外的问题,请重试");
            redirectAttributes.addFlashAttribute("errors", map);
            return "redirect:" + domainNameProperties.getSonLoginUrl();
        }
        //登录失败跳转 登录页面
        Map<String, String> map = new HashMap<>();
        map.put("msg", "code不能为空,请重试");
        redirectAttributes.addFlashAttribute("errors", map);
        return "redirect:" + domainNameProperties.getSonLoginUrl();
    }


    // 参数的封装
    public static Map<String, String> parseResponseEntity(String responseEntityStr) {
        Map<String, String> map = new HashMap<>();
        String[] strs = responseEntityStr.split("\\&");
        for (String str : strs) {
            String[] mapStrs = str.split("=");
            String value = null;
            String key = mapStrs[0];
            if (mapStrs.length > 1) {
                value = mapStrs[1];
            }
            map.put(key, value);
        }
        return map;
    }

}
