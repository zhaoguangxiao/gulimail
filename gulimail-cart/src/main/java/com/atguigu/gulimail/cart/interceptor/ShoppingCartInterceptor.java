package com.atguigu.gulimail.cart.interceptor;

import com.atguigu.common.vo.LoginUserVo;
import com.atguigu.gulimail.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static com.atguigu.common.constant.LoginUserConstant.LOGIN_USER_KEY;
import static com.atguigu.common.constant.ShoppingCartConstant.*;

/**
 * 使用TheadLocal 共享数据
 * 在执行目标方法之前,判断用户的登录状态
 * 并封装传递给controller
 */
@Component
public class ShoppingCartInterceptor implements HandlerInterceptor {




    @Value("${domain.name.domainName}")
    private String domainName;

    public static ThreadLocal<UserInfoTo> userInfoToThreadLocal = new ThreadLocal<>();


    /**
     * 在目标方法执行之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo infoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        LoginUserVo loginUserVo = (LoginUserVo) session.getAttribute(LOGIN_USER_KEY);
        if (null != loginUserVo) {
            //用户已登录
            infoTo.setUserId(loginUserVo.getId());
        }
        //用户未登录
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie item : cookies) {
                if (item.getName().equals(TEMPORARY_COOKIE_SHOPPING_CART_KEY)) {
                    infoTo.setUserKey(item.getValue());
                    infoTo.setTmpUser(true);
                }
            }
        }
        //创建临时用户的user-key
        if (StringUtils.isEmpty(infoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            infoTo.setUserKey(uuid);
        }
        //在目标方法执行之前
        userInfoToThreadLocal.set(infoTo);
        return true; //每个方法都放行
    }

    /**
     * 业务执行之后 --分配临时用户 让浏览器保存
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = userInfoToThreadLocal.get();
        if (!userInfoTo.isTmpUser()) {
            //持续的延长过期用户过期时间
            Cookie cookie = new Cookie(TEMPORARY_COOKIE_SHOPPING_CART_KEY, userInfoTo.getUserKey());
            cookie.setDomain(COOKIE_DOMAIN_NAME);
            cookie.setMaxAge(COOKIE_TMP_MAX_TIME);
            response.addCookie(cookie);
        }

    }
}
