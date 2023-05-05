package yamsroun.analyzer.rollout;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import yamsroun.analyzer.rollout.analyzer.RolloutMessageAnalyzer;
import yamsroun.analyzer.rollout.data.RolloutInfo;
import yamsroun.analyzer.rollout.data.RolloutType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class RolloutMessageAnalyzerExecutor {

    private final RolloutMessageAnalyzer messageAnalyzer;

    @EventListener(ApplicationReadyEvent.class)
    public void analyzeAllMessage() {
        messageAnalyzer.analyzeAllMessage();
        printResult(messageAnalyzer.getResult());
    }

    private void printResult(List<RolloutInfo> infos) {
        Map<String, Integer> stats = new LinkedHashMap<>();

        infos.forEach(info -> {
            String serviceName = String.format("%-20s", info.serviceName());
            LocalDateTime dateTime = info.rolloutDateTime();
            String imageTag = String.format("%-30s", info.imageTag());
            String rolloutDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            RolloutType rolloutType = info.rolloutType();
            String addingInfo = rolloutType.isDefault() ? "" : " -> " + rolloutType;
            System.out.printf("%s - %s:%s%s%n", rolloutDateTime, serviceName, imageTag, addingInfo);

            String date = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            String dateAndServiceName = date + ", " + serviceName;
            stats.putIfAbsent(dateAndServiceName, 0);
            Integer count = stats.get(dateAndServiceName);
            stats.put(dateAndServiceName, ++count);
        });

        System.out.println("------------------------------");
        stats.forEach((dateAndServiceName, count) ->
            System.out.println(dateAndServiceName + ", " + count)
        );
    }
}
