package org.reactivestreams.reactor.intergration.sink;

import org.reactivestreams.reactor.intergration.NotThreadSafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@NotThreadSafe
public class FaultyConsumer<T> implements Consumer<T> {

    private List<T> messages = new ArrayList<>();
    private Random random = new Random();

    @Override
    public void receive(T message) {
        if (random.nextBoolean()) {
            throw new RuntimeException("[Consumer]: Error occured during processing message");
        } else {
            messages.add(message);
        }
    }

    public List<T> getMessages() {
        return messages;
    }

}
