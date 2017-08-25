package org.reactivestreams.reactor.intergration.source;

import org.reactivestreams.reactor.intergration.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class PollingIntSource implements PollingSource<Integer> {

    private AtomicInteger generator = new AtomicInteger();

    @Override
    public Integer poll() {
        return generator.getAndIncrement();
    }
}
