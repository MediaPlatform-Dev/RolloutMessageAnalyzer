package yamsroun.analyzer.rollout.analyzer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import yamsroun.analyzer.client.SlackFeignClient;
import yamsroun.analyzer.client.data.SlackApiResponse;
import yamsroun.analyzer.client.data.SlackMessage;
import yamsroun.analyzer.rollout.data.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SlackRolloutMessageAnalyzer implements RolloutMessageAnalyzer {

    private static final Pattern IMAGE_TAG_LINK_PATTERN = Pattern.compile("<http://(.*)/(?<serviceName>.*):(?<imageTag>.*)>");

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
                        .forEach(field -> parseFieldAndAddResult(field, rolloutDateTime)));

            });
    }

    private boolean isImageTagField(SlackMessage.Attachment.Field field) {
        //return field.title().endsWith("-fleta");
        return !field.title().equals("Strategy");
    }

    private void parseFieldAndAddResult(SlackMessage.Attachment.Field field, LocalDateTime rolloutDateTime) {
        String imageTagLink = field.value();
        Matcher matcher = IMAGE_TAG_LINK_PATTERN.matcher(imageTagLink);
        if (matcher.matches()) {
            String serviceName = matcher.group("serviceName");
            String imageTag = matcher.group("imageTag");
            String buildTimeTag = imageTag.substring(imageTag.length() - 11);

            if (StringUtils.hasText(serviceName)) {
                RolloutType rolloutType = serviceImageBuildTimeTag.getRolloutType(serviceName, buildTimeTag);
                serviceImageBuildTimeTag.addBuildTimeTag(serviceName, buildTimeTag);
                result.add(new RolloutInfo(serviceName, rolloutDateTime, imageTag, rolloutType));
            }
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
