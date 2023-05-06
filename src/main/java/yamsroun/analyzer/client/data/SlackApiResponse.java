package yamsroun.analyzer.client.data;

import java.util.List;

public record SlackApiResponse(
    boolean ok,
    List<SlackMessage> messages,
    String error
) {

}
