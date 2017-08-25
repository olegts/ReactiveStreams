package org.reactivestreams.reactor.intergration.source;

import java.util.concurrent.atomic.AtomicInteger;

public class HotIntSource extends AbstractHotSource<Integer> {

    private final AtomicInteger counter = new AtomicInteger();

    public HotIntSource() {
        super();
        start();
    }

    public HotIntSource(Listener<Integer>... listeners) {
        super(listeners);
        start();
    }

    @Override
    Integer next() {
        return counter.getAndIncrement();
    }
}
