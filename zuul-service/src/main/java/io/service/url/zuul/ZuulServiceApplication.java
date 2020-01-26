package io.service.url.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class ZuulServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulServiceApplication.class, args);
    }
}