package io.service.url.hazelcast.config;

import com.hazelcast.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Value("${hazelcast.cpsystem.member.count:3}")
    private Integer cpSystemMemberCount;

    @Bean
    public Config hazelCastConfig() {
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");
        config.getCPSubsystemConfig().setCPMemberCount(cpSystemMemberCount);
        return config;
    }
}
