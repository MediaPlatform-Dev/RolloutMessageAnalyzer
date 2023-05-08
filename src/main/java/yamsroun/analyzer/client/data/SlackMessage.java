package yamsroun.analyzer.client.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SlackMessage(
    @JsonProperty("bot_id") String botId,
    String type,
    String text,
    String user,
    String ts,
    @JsonProperty("app_id") String appId,
    String team,
    @JsonProperty("bot_profile") BotProfile botProfile,
    List<Attachment> attachments
) {

    public record BotProfile(
        String name
    ) { }

    public record Attachment(
        String title,
        List<Field> fields
    ) {

        public record Field(
            String value,
            String title
        ) { }
    }
}
