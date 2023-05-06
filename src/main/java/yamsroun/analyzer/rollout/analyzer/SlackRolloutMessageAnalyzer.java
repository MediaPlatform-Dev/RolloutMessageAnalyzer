package yamsroun.analyzer.rollout.analyzer;

import lombok.RequiredArgsConstructor;
import yamsroun.analyzer.client.SlackFeignClient;
import yamsroun.analyzer.client.data.SlackApiResponse;
import yamsroun.analyzer.client.data.SlackMessage;
import yamsroun.analyzer.config.property.TargetServiceProperties;
import yamsroun.analyzer.rollout.data.RolloutInfo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
public class SlackRolloutMessageAnalyzer implements RolloutMessageAnalyzer {

    private final TargetServiceProperties serviceProperties;
    private final SlackFeignClient slackFeignClient;

    @Override
    public void analyzeAllMessage() {
        String channel = serviceProperties.slack().channel();
        SlackApiResponse response = slackFeignClient.getConversationsHistory(channel);
        List<SlackMessage> messages = response.messages();
        Collections.reverse(messages);
        messages.forEach(msg -> {
            String text = msg.text();
            LocalDateTime localDateTime = getLocalDateTime(msg.ts());
            System.out.println(">>> msg=" + msg);

            System.out.println(">>> " + text);
            System.out.println(localDateTime);
        });
        System.out.println(">>> size=" + response.messages().size());
    }

    private LocalDateTime getLocalDateTime(String timestampMillis) {
        String[] timestampSplit = timestampMillis.split("\\.", -1);
        long timestamp = Long.parseLong(timestampSplit[0] + "000");
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }

    @Override
    public List<RolloutInfo> getResult() {
        return Collections.emptyList();
    }
}
