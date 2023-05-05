package yamsroun.analyzer.rollout.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yamsroun.analyzer.rollout.analyzer.RolloutMessageAnalyzer;
import yamsroun.analyzer.rollout.analyzer.TextFileRolloutMessageAnalyzer;

@Configuration
public class RolloutMessageAnalyzerConfig {

    @Bean
    public RolloutMessageAnalyzer textFileRolloutMessageAnalyzer() {
        return new TextFileRolloutMessageAnalyzer();
    }
}
