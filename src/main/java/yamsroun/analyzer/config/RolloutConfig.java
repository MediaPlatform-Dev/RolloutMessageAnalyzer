package yamsroun.analyzer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yamsroun.analyzer.client.SlackFeignClient;
import yamsroun.analyzer.config.property.TargetServiceProperties;
import yamsroun.analyzer.rollout.analyzer.*;

@Configuration
@ConfigurationPropertiesScan(basePackages = "yamsroun.analyzer")
@RequiredArgsConstructor
public class RolloutConfig {

    private final TargetServiceProperties serviceProperties;
    private final SlackFeignClient slackFeignClient;

    //@Bean
    public RolloutMessageAnalyzer textFileRolloutMessageAnalyzer() {
        return new TextFileRolloutMessageAnalyzer();
    }

    @Bean
    public RolloutMessageAnalyzer slackRolloutMessageAnalyzer() {
        return new SlackRolloutMessageAnalyzer(serviceProperties, slackFeignClient);
    }
}
