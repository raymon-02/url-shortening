package io.service.url.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class HazelcastConfig {

    @Value("${hazelcast.cpsystem.member.count:3}")
    private Integer cpSystemMemberCount;

    @Bean
    public Config hazelCastConfig(
            @Autowired HazelcastProperties hazelcastProperties
    ) {
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");
        config.getCPSubsystemConfig().setCPMemberCount(cpSystemMemberCount);

        NetworkConfig network = config.getNetworkConfig();
        network.setPort(5701).setPortCount(20);
        network.setPortAutoIncrement(true);
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        hazelcastProperties.getMembers().forEach(member -> join.getTcpIpConfig().addMember(member));
        join.getTcpIpConfig().setEnabled(true);

        log.info("Hazelcast CPSystem members: {}", cpSystemMemberCount);
        return config;
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "hazelcast")
    static class HazelcastProperties {
        private List<String> members = new ArrayList<>();
    }
}
