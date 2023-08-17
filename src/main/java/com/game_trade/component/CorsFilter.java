package com.game_trade.component;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 跨域过滤器
 */
@Component

@Slf4j
public class CorsFilter implements Filter {

    static final String OPTIONS = "OPTIONS";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // 获取请求头
        String origin = request.getHeader("origin");
        log.info("*********************************请求头：" + origin);
        if (origin ==null) {
            origin = request.getHeader("Referer");
        }
        // 允许指定域访问跨域资源
        response.setHeader("Access-Control-Allow-Origin", origin);
        // 允许所有请求方式
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
        // 有效时间
        response.setHeader("Access-Control-Max-Age", "3600");
        // 允许的header参数
        response.setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, authority, content-type, version-info, x-requested-with,satoken");

        // 打印请求方式
        log.info("*********************************请求方式：" + request.getMethod());

        // 如果是预检请求，直接返回
        if (OPTIONS.equals(request.getMethod())) {
            log.info("=======================浏览器发来了OPTIONS预检请求==========");
            response.getWriter().print("");
            return;
        }

        log.info("*********************************过滤器被使用**************************");

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
