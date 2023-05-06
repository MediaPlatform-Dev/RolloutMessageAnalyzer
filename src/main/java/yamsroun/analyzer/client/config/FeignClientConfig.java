package yamsroun.analyzer.client.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "yamsroun.analyzer.client")
public class FeignClientConfig {
}
