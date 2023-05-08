package yamsroun.analyzer.config.property;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

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
        private final MessageHistory history;

        public SlackTargetService(
            String host,
            String token,
            String channel,
            MessageHistory history
        ) {
            super(host);
            this.token = token;
            this.channel = channel;
            this.history = history;
        }

        public String token() {
            return token;
        }

        public String channel() {
            return channel;
        }

        public MessageHistory history() {
            return history;
        }

        public static class MessageHistory {

            private final String oldest;
            private final int limit;

            public MessageHistory(String oldest, @DefaultValue("100") int limit) {
                this.oldest = oldest;
                this.limit = limit;
            }

            public String oldest() {
                return oldest;
            }

            public int limit() {
                return limit;
            }
        }
    }
}
