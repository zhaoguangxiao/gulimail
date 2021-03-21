package com.atguigu.gulimail.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/** todo webflux 响应式编程
 * 网关自定义回调
 */
@Configuration
public class MySentinelGatewayConfig {

    public MySentinelGatewayConfig() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            //网关限流了请求,就会调用回调 mono flux
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                HashMap<String, String> map = new HashMap<>();
                map.put("code", "10003");
                map.put("message", "请求流量过大,无法访问");
                String errorResult = JSON.toJSONString(map);
                return ServerResponse.ok().body(Mono.just(errorResult), String.class);
            }
        });
    }
}
