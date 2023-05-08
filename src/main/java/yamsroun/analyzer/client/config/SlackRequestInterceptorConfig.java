package yamsroun.analyzer.client.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import yamsroun.analyzer.config.property.TargetServiceProperties;

import java.util.*;

@RequiredArgsConstructor
public class SlackRequestInterceptorConfig {

    private final TargetServiceProperties serviceProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        String token = serviceProperties.slack().token();
        Map<String, Collection<String>> queries = Map.of(
            "channel", List.of(serviceProperties.slack().channel()),
            "oldest", List.of(serviceProperties.slack().history().oldest()),
            "limit", List.of(String.valueOf(serviceProperties.slack().history().limit()))
        );

        return requestTemplate -> {
            requestTemplate
                .header(HttpHeaders.AUTHORIZATION, token)
                .queries(queries);
        };
    }
}
