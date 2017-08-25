package org.reactivestreams.reactor.intergration.source;

import org.jooq.lambda.Unchecked;
import org.reactivestreams.reactor.Util;

import java.util.concurrent.TimeUnit;

public abstract class SlowPollingSource<T> implements PollingSource<T> {

    @Override
    public T poll() {
        Util.wait(1, TimeUnit.SECONDS);
        return next();
    }

    abstract T next();
}
