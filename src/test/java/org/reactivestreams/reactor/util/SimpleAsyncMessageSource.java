package org.reactivestreams.reactor.util;

import org.reactivestreams.reactor.Util;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SimpleAsyncMessageSource implements Runnable {

    private final AtomicInteger counter = new AtomicInteger();
    private final Listener listener;

    public SimpleAsyncMessageSource(Listener listener) {
        this.listener = listener;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            Util.printlnThread("Emitted: " + counter.get());
            listener.onReceive(counter.getAndIncrement());
            Util.wait(1, SECONDS);
        }
    }

    public interface Listener {
        void onReceive(Object msg);
    }
}
