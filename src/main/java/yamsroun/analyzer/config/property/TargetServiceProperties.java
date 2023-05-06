package yamsroun.analyzer.config.property;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service")
@RequiredArgsConstructor
public class TargetServiceProperties {

    private final SlackTargetService slack;

    public SlackTargetService slack() {
        return slack;
    }

    @RequiredArgsConstructor
    public static class TargetService {

        private final String host;

        public String host() {
            return host;
        }
    }

    public static class SlackTargetService extends TargetService {

        private final String token;
        private final String channel;

        public SlackTargetService(String host, String token, String channel) {
            super(host);
            this.token = token;
            this.channel = channel;
        }

        public String token() {
            return token;
        }

        public String channel() {
            return channel;
        }
    }
}
