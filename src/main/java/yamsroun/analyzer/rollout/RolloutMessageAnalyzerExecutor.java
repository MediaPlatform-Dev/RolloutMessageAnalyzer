package yamsroun.analyzer.rollout;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import yamsroun.analyzer.rollout.analyzer.RolloutMessageAnalyzer;
import yamsroun.analyzer.rollout.data.RolloutInfo;
import yamsroun.analyzer.rollout.data.RolloutType;

import java.time.LocalDate;
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
        printResult();
    }

    private void printResult() {
        List<RolloutInfo> result = messageAnalyzer.getResult();
        Map<DateAndServiceName, Integer> stats1 = new LinkedHashMap<>();
        Map<String, Integer> stats2 = new HashMap<>();

        result.forEach(info -> {
            String serviceName = info.serviceName();
            LocalDateTime dateTime = info.rolloutDateTime();
            String imageTag = info.imageTag();
            String rolloutDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            RolloutType rolloutType = info.rolloutType();
            String addingInfo = rolloutType.isDefault() ? "" : " -> " + rolloutType;
            System.out.printf("%s - %-26s:%30s%s%n", rolloutDateTime, serviceName, imageTag, addingInfo);

            DateAndServiceName dateAndServiceName = new DateAndServiceName(dateTime.toLocalDate(), serviceName);
            stats1.putIfAbsent(dateAndServiceName, 0);
            Integer count1 = stats1.get(dateAndServiceName);
            stats1.put(dateAndServiceName, ++count1);

            stats2.putIfAbsent(serviceName, 0);
            Integer count2 = stats2.get(serviceName);
            stats2.put(serviceName, ++count2);
        });

        System.out.println("------------------------------");
        stats1.forEach((dateAndServiceName, count) -> {
            LocalDate date = dateAndServiceName.date();
            String serviceName = dateAndServiceName.serviceName();
            System.out.printf("%s, %-26s, %d%n", date, serviceName, count);
        });

        System.out.println("------------------------------");
        stats2.forEach((serviceName, count) -> {
            System.out.printf("%-26s, %d%n", serviceName, count);
        });
    }

    record DateAndServiceName(LocalDate date, String serviceName) {
    }
}
