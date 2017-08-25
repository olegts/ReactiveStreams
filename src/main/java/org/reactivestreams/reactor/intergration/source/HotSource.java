package org.reactivestreams.reactor.intergration.source;

public interface HotSource<T> {

    void subscribe(Listener<T> listener);

}
