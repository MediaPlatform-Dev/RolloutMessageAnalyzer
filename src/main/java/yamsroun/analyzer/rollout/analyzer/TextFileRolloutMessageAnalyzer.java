package yamsroun.analyzer.rollout.analyzer;

import lombok.Getter;
import yamsroun.analyzer.rollout.data.*;

import java.time.*;
import java.util.LinkedList;
import java.util.List;

public class TextFileRolloutMessageAnalyzer implements RolloutMessageAnalyzer {

    private static final String PLUS_DAY_MESSAGE = "---PLUS_DAY";

    @Getter
    private final List<RolloutInfo> result = new LinkedList<>();

    private final ServiceImageBuildTimeTag serviceImageBuildTimeTag = new ServiceImageBuildTimeTag();


    private String currentMessage;
    private String serviceName;
    private String imageTag;
    private LocalTime rolloutTime;

    private boolean first = true;
    private boolean foundService = false;
    private RolloutType rolloutType = RolloutType.DEPLOYMENT;
    private String prevMessage;
    private LocalDate currentDate = LocalDate.of(2023, Month.APRIL, 27);


    @Override
    public void analyzeAllMessage() {
        TextFileMessagerReader messageReader = new TextFileMessagerReader();
        String line;
        while ((line = messageReader.read()) != null) {
            analyzeMessage(line);
        }
        done();
    }

    private void analyzeMessage(String message) {
        currentMessage = message;
        if (isPlusDayMessage()) {
            setToNextDay();
        }
        if (isRolloutMessage()) {
            addResult();
            setTime();
            resetStatus();
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

    private void resetStatus() {
        first = false;
        foundService = false;
        rolloutType = RolloutType.DEPLOYMENT;
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
        String[] split = currentMessage.split(":", -1);
        imageTag = split[split.length - 1];
        int imageTagLength = imageTag.length();
        String buildTimeTag = imageTag.substring(imageTagLength - 11, imageTagLength);
        rolloutType = serviceImageBuildTimeTag.getRolloutType(serviceName, buildTimeTag);
        serviceImageBuildTimeTag.addBuildTimeTag(serviceName, buildTimeTag);
    }

    private void addResult() {
        if (first || !foundService) {
            return;
        }
        LocalDateTime rolloutDateTime = LocalDateTime.of(currentDate, rolloutTime);
        result.add(new RolloutInfo(serviceName, rolloutDateTime, imageTag, rolloutType));
    }

}
