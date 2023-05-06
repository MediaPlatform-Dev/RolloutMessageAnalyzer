package yamsroun.analyzer.client.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import yamsroun.analyzer.config.property.TargetServiceProperties;

@RequiredArgsConstructor
public class SlackRequestInterceptorConfig {

    private final TargetServiceProperties serviceProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        String token = serviceProperties.slack().token();
        return requestTemplate -> {
            requestTemplate
                .header(HttpHeaders.AUTHORIZATION, token);
        };
    }
}
