package org.reactivestreams.reactor.intergration.source;

public interface PollingSource<T> {

    T poll();

}
