package yamsroun.analyzer.rollout.analyzer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import yamsroun.analyzer.client.SlackFeignClient;
import yamsroun.analyzer.client.data.SlackApiResponse;
import yamsroun.analyzer.client.data.SlackMessage;
import yamsroun.analyzer.rollout.data.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
public class SlackRolloutMessageAnalyzer implements RolloutMessageAnalyzer {

    private final SlackFeignClient slackFeignClient;

    @Getter
    private final List<RolloutInfo> result = new LinkedList<>();

    private final ServiceImageBuildTimeTag serviceImageBuildTimeTag = new ServiceImageBuildTimeTag();

    @Override
    public void analyzeAllMessage() {
        SlackApiResponse response = slackFeignClient.getConversationsHistory();
        List<SlackMessage> messages = response.messages();
        Collections.reverse(messages);

        messages.stream()
            .filter(this::isArgoNotificationMessage)
            .forEach(msg -> {
                LocalDateTime rolloutDateTime = getRolloutDateTime(msg.ts());
                msg.attachments()
                    .forEach(a -> a.fields()
                        .stream()
                        .filter(this::isImageTagField)
                        .forEach(field -> parseFieldAndAddResult(rolloutDateTime, field)));

            });
    }

    private boolean isImageTagField(SlackMessage.Attachment.Field f) {
        return f.title().endsWith("-fleta");
    }

    private void parseFieldAndAddResult(LocalDateTime rolloutDateTime, SlackMessage.Attachment.Field field) {
        RolloutType rolloutType = null;

        String imageTagLink = field.value();
        String[] imageTagLinkSplit = imageTagLink.split("/", -1);
        String serviceNameAndImageTag = imageTagLinkSplit[imageTagLinkSplit.length - 1];
        serviceNameAndImageTag = serviceNameAndImageTag.substring(0, serviceNameAndImageTag.length() - 1);
        String[] serviceNameAndImageTagSplit = serviceNameAndImageTag.split(":", -1);
        String serviceName = serviceNameAndImageTagSplit[0];
        String imageTag = serviceNameAndImageTagSplit[1];
        int imageTagLength = imageTag.length();
        String buildTimeTag = imageTag.substring(imageTagLength - 11, imageTagLength);
        rolloutType = serviceImageBuildTimeTag.getRolloutType(serviceName, buildTimeTag);
        serviceImageBuildTimeTag.addBuildTimeTag(serviceName, buildTimeTag);

        if (serviceName != null) {
            result.add(new RolloutInfo(serviceName, rolloutDateTime, imageTag, rolloutType));
        }
    }

    private boolean isArgoNotificationMessage(SlackMessage msg) {
        return msg.botProfile() != null && msg.botProfile().name().equals("Argo Notifications");
    }

    private LocalDateTime getRolloutDateTime(String timestampMillis) {
        String[] timestampSplit = timestampMillis.split("\\.", -1);
        long timestamp = Long.parseLong(timestampSplit[0] + "000");
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }
}
