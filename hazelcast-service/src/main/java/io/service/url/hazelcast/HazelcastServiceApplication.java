package io.service.url.hazelcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class HazelcastServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HazelcastServiceApplication.class, args);
    }
}
