package yamsroun.analyzer.client.data;

import java.util.List;

//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SlackMessage(
    String bot_id,
    String type,
    String text,
    String user,
    String ts,
    String appId,
    String team,
    BotProfile botProfile,
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
