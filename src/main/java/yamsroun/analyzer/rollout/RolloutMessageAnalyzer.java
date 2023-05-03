package yamsroun.analyzer.rollout;

import lombok.Getter;

import java.time.*;
import java.util.*;

class RolloutMessageAnalyzer {

    private static final String PLUS_DAY_MESSAGE = "---PLUS_DAY";

    @Getter
    private final List<RolloutInfo> result = new LinkedList<>();

    private final ServiceNameImageBuildTime serviceNameImageBuildTime = new ServiceNameImageBuildTime();


    private String currentMessage;
    private String serviceName;
    private LocalTime rolloutTime;

    private boolean first = true;
    private boolean foundService = false;
    private boolean rollbackRollout = false;
    private String prevMessage;
    private LocalDate currentDate = LocalDate.of(2023, Month.APRIL, 27);

    public void analyzeMessage(String message) {
        currentMessage = message;
        if (isPlusDayMessage()) {
            setToNextDay();
        }
        if (isRolloutMessage()) {
            addResult();
            setTime();
            first = false;
            foundService = false;
        }
        if (isServiceNameMessage()) {
            setServiceName();
        }
        if (isImageTagMessage()) {
            setImageTag();
        }
        prevMessage = currentMessage;
    }

    public void done() {
        addResult();
    }

    private boolean isPlusDayMessage() {
        return currentMessage.equals(PLUS_DAY_MESSAGE);
    }

    private void setToNextDay() {
        currentDate = currentDate.plusDays(1);
    }

    private boolean isRolloutMessage() {
        return currentMessage.startsWith("Rollout ") && currentMessage.endsWith(" has been completed.");
    }

    private void setTime() {
        String time = prevMessage;
        int lineLength = time.length();
        String hour = time.substring(lineLength - 5, lineLength - 3);
        String minute = time.substring(lineLength - 2, lineLength);
        rolloutTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute));
    }

    private boolean isServiceNameMessage() {
        return currentMessage.endsWith("-fleta");
    }

    private void setServiceName() {
        int lineLength = currentMessage.length();
        serviceName = currentMessage.substring(0, lineLength - 6);
        foundService = true;
    }

    private boolean isImageTagMessage() {
        return currentMessage.startsWith("029480618840.");
    }

    private void setImageTag() {
        if (!foundService) {
            return;
        }
        String[] split = currentMessage.split("/", -1);
        String imageTag = split[split.length - 1];
        int imageTagLength = imageTag.length();
        String buildTimeTag = imageTag.substring(imageTagLength - 11, imageTagLength);

        String lastBuildTimeTag = serviceNameImageBuildTime.get(serviceName);
        if (lastBuildTimeTag != null && lastBuildTimeTag.compareTo(buildTimeTag) > 0) {
            rollbackRollout = true;
            //TODO
            System.out.printf(">>> %s ROLLBACK=%s -> %s%n", serviceName, lastBuildTimeTag, buildTimeTag);
        }
        serviceNameImageBuildTime.put(serviceName, buildTimeTag);
    }

    private void addResult() {
        if (first || !foundService) {
            return;
        }
        result.add(new RolloutInfo(serviceName, LocalDateTime.of(currentDate, rolloutTime)));
    }


    record RolloutInfo(String serviceName, LocalDateTime rolloutDateTime) {

    }


    static class ServiceNameImageBuildTime {

        private final Map<String, String> map = new HashMap<>();

        void put(String serviceName, String buildTime) {
            map.put(serviceName, buildTime);
        }

        String get(String serviceName) {
            return map.get(serviceName);
        }
    }
}
