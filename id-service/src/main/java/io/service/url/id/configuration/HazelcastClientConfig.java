package io.service.url.id.configuration;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class HazelcastClientConfig {

    @Bean
    public ClientConfig clientConfig(
            @Autowired HazelcastProperties hazelcastProperties
    ) {
        ClientConfig clientConfig = new ClientConfig();
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        networkConfig.addAddress(hazelcastProperties.getMembers().toArray(new String[0]))
                .setSmartRouting(true)
                .setRedoOperation(hazelcastProperties.isRedoOperation())
                .setConnectionTimeout(hazelcastProperties.getConnectionTimeout())
                .setConnectionAttemptLimit(hazelcastProperties.getConnectionAttempts());

        return clientConfig;
    }

    @Bean("hzInstance")
    public HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }


    @Data
    @Component
    @ConfigurationProperties(prefix = "hazelcast")
    static class HazelcastProperties {
        private List<String> members = new ArrayList<>();
        private boolean redoOperation = true;
        private int connectionTimeout = 5000;
        private int connectionAttempts = 5;
    }
}
