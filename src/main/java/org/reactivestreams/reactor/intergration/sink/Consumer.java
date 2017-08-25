package org.reactivestreams.reactor.intergration.sink;

import java.util.List;

public interface Consumer<T> {

    void receive(T message);

    List<T> getMessages();

}
