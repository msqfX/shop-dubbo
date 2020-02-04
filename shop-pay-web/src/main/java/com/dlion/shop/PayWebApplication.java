package com.dlion.shop;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lzy
 * @date 2020/2/4
 */
@SpringBootApplication
@EnableDubboConfiguration
public class PayWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayWebApplication.class, args);
    }
}
