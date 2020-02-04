package com.dlion.shop;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.dlion.shop.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author lzy
 * @date 2020/2/3
 */
@SpringBootApplication
@EnableDubboConfiguration
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1,1);
    }
}
