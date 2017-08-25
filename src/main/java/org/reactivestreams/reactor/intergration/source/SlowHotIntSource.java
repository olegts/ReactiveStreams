package org.reactivestreams.reactor.intergration.source;

import java.util.concurrent.atomic.AtomicInteger;

public class SlowHotIntSource extends AbstractSlowHotSource<Integer> {

    private final AtomicInteger counter = new AtomicInteger();

    public SlowHotIntSource() {
        super();
        start();
    }

    public SlowHotIntSource(Listener<Integer>... listeners) {
        super(listeners);
        start();
    }

    @Override
    Integer next() {
        return counter.getAndIncrement();
    }

}
