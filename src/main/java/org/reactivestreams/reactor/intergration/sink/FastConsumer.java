package org.reactivestreams.reactor.intergration.sink;

import org.reactivestreams.reactor.intergration.NotThreadSafe;

import java.util.ArrayList;
import java.util.List;

@NotThreadSafe
public class FastConsumer<T> implements Consumer<T> {

    private List<T> messages = new ArrayList<>();

    @Override
    public void receive(T message) {
        messages.add(message);
    }

    public List<T> getMessages() {
        return messages;
    }
}
