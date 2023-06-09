package yamsroun.analyzer.rollout.analyzer;

import jakarta.annotation.PreDestroy;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

public class TextFileMessagerReader {

    private final BufferedReader reader;

    public TextFileMessagerReader() {
        try {
            String readFile = "prd-deployment.txt";
            File file = new ClassPathResource(readFile).getFile();
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void release() throws IOException {
        reader.close();
    }
}
