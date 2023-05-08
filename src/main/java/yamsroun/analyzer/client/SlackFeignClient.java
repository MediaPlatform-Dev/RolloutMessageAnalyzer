package yamsroun.analyzer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yamsroun.analyzer.client.config.SlackRequestInterceptorConfig;
import yamsroun.analyzer.client.data.SlackApiResponse;

@FeignClient(name = "slack", configuration = {SlackRequestInterceptorConfig.class})
public interface SlackFeignClient {

    @GetMapping(path = "/api/conversations.history?channel={channelId}&oldest={oldest}&limit={limit}")
    SlackApiResponse getConversationsHistory(
        @RequestParam String channelId,
        @RequestParam String oldest,
        @RequestParam int limit
    );
}
