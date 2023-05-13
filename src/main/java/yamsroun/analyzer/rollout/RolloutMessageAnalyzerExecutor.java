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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Map<DateAndServiceName, Integer> stats = new LinkedHashMap<>();

        result.forEach(info -> {
            String serviceName = info.serviceName();
            LocalDateTime dateTime = info.rolloutDateTime();
            String imageTag = info.imageTag();
            String rolloutDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            RolloutType rolloutType = info.rolloutType();
            String addingInfo = rolloutType.isDefault() ? "" : " -> " + rolloutType;
            System.out.printf("%s - %-20s:%30s%s%n", rolloutDateTime, serviceName, imageTag, addingInfo);

            DateAndServiceName dateAndServiceName = new DateAndServiceName(dateTime.toLocalDate(), serviceName);
            stats.putIfAbsent(dateAndServiceName, 0);
            Integer count = stats.get(dateAndServiceName);
            stats.put(dateAndServiceName, ++count);
        });

        System.out.println("------------------------------");
        stats.forEach((dateAndServiceName, count) -> {
            LocalDate date = dateAndServiceName.date();
            String serviceName = dateAndServiceName.serviceName();
            System.out.printf("%s, %-20s, %d%n", date, serviceName, count);
        });
    }

    record DateAndServiceName(LocalDate date, String serviceName) {
    }
}
