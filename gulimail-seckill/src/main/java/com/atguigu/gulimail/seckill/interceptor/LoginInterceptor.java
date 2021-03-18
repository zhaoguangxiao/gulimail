package com.atguigu.gulimail.seckill.interceptor;


import com.atguigu.common.vo.LoginUserVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.atguigu.common.constant.LoginUserConstant.LOGIN_USER_KEY;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<LoginUserVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //放行某些请求 不需要登录就能访问
        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/checkseckill", uri);

        if (match) {
            //获取当前用户
            Object attribute = request.getSession().getAttribute(LOGIN_USER_KEY);
            if (null != attribute) {
                //将当前用户 放入全部访问
                threadLocal.set((LoginUserVo) attribute);
                return true;
            } else {
                //未登录 去登录
                //保存消息
                request.getSession().setAttribute("msg", "请先去登录");
                response.sendRedirect("http://auth.gulimail.com/login.html");
                return false;
            }
        }
        return true; //放行全部请求
    }

}
