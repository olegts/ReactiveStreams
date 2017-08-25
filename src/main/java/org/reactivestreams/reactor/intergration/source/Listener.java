package org.reactivestreams.reactor.intergration.source;

public interface Listener<T> {

    void onReceive(T message);

}
