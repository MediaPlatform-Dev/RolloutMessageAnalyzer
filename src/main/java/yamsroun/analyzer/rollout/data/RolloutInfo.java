package yamsroun.analyzer.rollout.data;

import java.time.LocalDateTime;

public record RolloutInfo(
    String serviceName,
    LocalDateTime rolloutDateTime,
    String imageTag,
    RolloutType rolloutType
) {

}
