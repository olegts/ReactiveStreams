package org.reactivestreams.reactor.intergration.source;

import org.reactivestreams.reactor.intergration.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class SlowPollingIntSource extends SlowPollingSource<Integer> {

    private AtomicInteger generator = new AtomicInteger();

    @Override
    Integer next() {
        return generator.getAndIncrement();
    }
}
