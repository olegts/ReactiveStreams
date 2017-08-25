package org.reactivestreams.reactor.intergration.sink;

import org.jooq.lambda.Unchecked;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.intergration.NotThreadSafe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@NotThreadSafe
public class SlowConsumer<T> implements Consumer<T> {

    private List<T> messages = new ArrayList<>();

    @Override
    public void receive(T message) {
        messages.add(message);
        Util.wait(1, TimeUnit.SECONDS);
    }

    public List<T> getMessages() {
        return messages;
    }
}
