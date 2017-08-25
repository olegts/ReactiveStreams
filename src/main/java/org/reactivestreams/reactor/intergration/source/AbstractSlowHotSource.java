package org.reactivestreams.reactor.intergration.source;

import org.jooq.lambda.Unchecked;
import org.reactivestreams.reactor.Util;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSlowHotSource<T> extends AbstractHotSource<T> {

    public AbstractSlowHotSource() {
        super();
    }

    public AbstractSlowHotSource(Listener<T>... listeners) {
        super(listeners);
    }

    @Override
    public void run() {
        for (;;) {
            final T next = next();
            listeners.forEach(l -> l.onReceive(next));
            Util.wait(10, TimeUnit.MILLISECONDS);
        }
    }
}
