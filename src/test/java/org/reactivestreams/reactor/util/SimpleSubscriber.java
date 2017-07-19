package org.reactivestreams.reactor.util;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.reactor.Util;

public class SimpleSubscriber<T> implements Subscriber<T> {

    private String name;
    private long initialDemand;

    public SimpleSubscriber(String name, long initialDemand) {
        this.name = name;
        this.initialDemand = initialDemand;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(initialDemand);
    }

    @Override
    public void onNext(T next) {
        Util.printlnThread(String.format("[%s] Received: %s", name, next));
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(String.format("[%s] Thrown: %s", name, throwable));
    }

    @Override
    public void onComplete() {
        System.out.println(String.format("[%s] Completed!"));
    }
}
