package io.service.url.shortening.configuration;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastClientConfig {

    @Bean
    public ClientConfig clientConfig(
            @Value("${hazelcast.member}") String hazelcastMember
    ) {
        ClientConfig clientConfig = new ClientConfig();
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        networkConfig.addAddress(hazelcastMember)
                .setSmartRouting(true)
                .setRedoOperation(true)
                .setConnectionTimeout(5000)
                .setConnectionAttemptLimit(5);

        return clientConfig;
    }

    @Bean("hzInstance")
    public HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }

}
