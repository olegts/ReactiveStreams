package org.reactivestreams.reactor.intergration.sink;

import org.reactivestreams.reactor.intergration.NotThreadSafe;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NotThreadSafe
public class ThrottledConsumer<T> implements Consumer<T> {

    //private static final int LIMIT_MESSAGES_NUMBER_PER_SECOND = 10;
    private int limitPerSecond;
    private int cursor;
    private TimestampedMessage[] messagesCache;
    private List<T> messages = new ArrayList<>();

    public ThrottledConsumer(int limit) {
        limitPerSecond = limit;
        messagesCache = new TimestampedMessage[limit];
    }

    @Override
    public void receive(T message) {
        if (messagesCache[cursor] == null || Duration.between(messagesCache[cursor].timestamp, LocalTime.now()).toMillis() > 1000) {
            messagesCache[cursor] = new TimestampedMessage(message, LocalTime.now());
            cursor = (cursor + 1) % limitPerSecond;
            messages.add(message);
        } else {
            throw new RejectedMessageException("Messages rate exceeded limits");
        }
    }

    @Override
    public List<T> getMessages() {
        return messages;
    }

    static class TimestampedMessage<T> {
        private T message;
        private LocalTime timestamp;

        public TimestampedMessage(T message, LocalTime timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    static class RejectedMessageException extends RuntimeException {
        public RejectedMessageException(String message) {
            super(message);
        }
    }
}
