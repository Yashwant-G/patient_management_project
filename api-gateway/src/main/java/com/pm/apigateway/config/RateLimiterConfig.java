package com.pm.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver(){
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        //get the ip address of the requesting host machine
        //google.com/142.250.183.14- getAddress() example
        // /192.168.1.10:52344-getRemoteAddress() example
    }
}
