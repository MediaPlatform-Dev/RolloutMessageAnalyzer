package yamsroun.analyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yamsroun.analyzer.rollout.analyzer.RolloutMessageAnalyzer;
import yamsroun.analyzer.rollout.analyzer.TextFileRolloutMessageAnalyzer;

@Configuration
public class RolloutConfig {

    @Bean
    public RolloutMessageAnalyzer textFileRolloutMessageAnalyzer() {
        return new TextFileRolloutMessageAnalyzer();
    }
}
