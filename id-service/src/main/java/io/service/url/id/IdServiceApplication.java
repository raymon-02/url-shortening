package io.service.url.id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class IdServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdServiceApplication.class, args);
    }
}
