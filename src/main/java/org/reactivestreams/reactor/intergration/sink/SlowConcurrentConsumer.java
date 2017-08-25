package org.reactivestreams.reactor.intergration.sink;

import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.intergration.ThreadSafe;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@ThreadSafe
public class SlowConcurrentConsumer<T> implements Consumer<T> {

    private List<T> messages = new CopyOnWriteArrayList<>();

    @Override
    public void receive(T message) {
        messages.add(message);
        Util.wait(1, TimeUnit.SECONDS);
    }

    public List<T> getMessages() {
        return messages;
    }

}
