package org.reactivestreams.reactor.intergration.source;

import java.util.Random;

public abstract class FaultyPollingSource<T> implements PollingSource<T> {

    private Random random = new Random();

    @Override
    public T poll() {
        T message = next();
        if (random.nextBoolean()) {
            throw new RuntimeException("[Producer]: Error occured during emiting message");
        } else {
            return message;
        }
    }

    abstract T next();
}
