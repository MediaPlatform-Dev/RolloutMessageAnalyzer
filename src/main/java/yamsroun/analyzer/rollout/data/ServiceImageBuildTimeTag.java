package yamsroun.analyzer.rollout.data;

import java.util.*;

public class ServiceImageBuildTimeTag {

    private final Map<String, List<String>> map = new HashMap<>();

    public void addBuildTimeTag(String serviceName, String buildTime) {
        map.putIfAbsent(serviceName, new LinkedList<>());
        List<String> buildTimeTags = map.get(serviceName);
        buildTimeTags.add(buildTime);
    }

    public String getLastBuildTimeTag(String serviceName) {
        List<String> buildTimeTags = map.get(serviceName);
        if (buildTimeTags == null) {
            return null;
        }
        return buildTimeTags.get(buildTimeTags.size() - 1);
    }

    public boolean existsBuildTimeTag(String serviceName, String buildTime) {
        List<String> buildTimeTags = map.get(serviceName);
        if (buildTimeTags == null) {
            return false;
        }
        return buildTimeTags.contains(buildTime);
    }

    public RolloutType getRolloutType(String serviceName, String buildTimeTag) {
        String lastBuildTimeTag = getLastBuildTimeTag(serviceName);
        if (lastBuildTimeTag != null) {
            if (lastBuildTimeTag.compareTo(buildTimeTag) > 0) {
                return RolloutType.ROLLBACK;
            } else if (existsBuildTimeTag(serviceName, buildTimeTag)) {
                return RolloutType.RE_DEPLOYMENT;
            }
        }
        return RolloutType.getDefalut();
    }
}
