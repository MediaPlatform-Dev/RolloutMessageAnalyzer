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
}
