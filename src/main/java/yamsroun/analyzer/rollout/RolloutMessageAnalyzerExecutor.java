package yamsroun.analyzer.rollout;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class RolloutMessageAnalyzerExecutor {

    @PostConstruct
    void init() throws IOException {
        analyzeFileMessage();
    }

    void analyzeFileMessage() throws IOException {
        RolloutMessageAnalyzer messageAnalyzer = new RolloutMessageAnalyzer();
        analyzeMessageOfFile(messageAnalyzer);
        printResult(messageAnalyzer.getResult());
    }

    private void analyzeMessageOfFile(RolloutMessageAnalyzer messageAnalyzer) throws IOException {
        String readFile = "prd-deployment.txt";
        File file = new ClassPathResource(readFile).getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messageAnalyzer.analyzeMessage(line);
            }
            messageAnalyzer.done();
        }
    }

    private void printResult(List<RolloutMessageAnalyzer.RolloutInfo> infos) {
        Map<String, Integer> stats = new LinkedHashMap<>();

        infos.forEach(info -> {
            String serviceName = info.serviceName();
            LocalDateTime dateTime = info.rolloutDateTime();
            String rolloutDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            System.out.printf("%s - %s%n", serviceName, rolloutDateTime);

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
