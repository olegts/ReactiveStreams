package org.reactivestreams.reactor.util;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.reactor.Util;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SingleSubscriptionSimplePublisher implements Publisher<Integer>, Subscription, Runnable {

    private Subscriber<? super Integer> subscriber;
    private volatile boolean isActive = true;

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        this.subscriber = subscriber;
        subscriber.onSubscribe(this);
    }

    @Override
    public void request(long l) {
        new Thread(this).start();
    }

    @Override
    public void cancel() {
        isActive = false;
    }

    @Override
    public void run() {
        int i = 0;
        while (isActive) {
            subscriber.onNext(i++);
            Util.wait(1, SECONDS);
        }
    }
}
