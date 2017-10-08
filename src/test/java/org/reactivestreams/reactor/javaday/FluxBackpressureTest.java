package org.reactivestreams.reactor.javaday;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.util.SimpleAsyncMessageSource;
import org.reactivestreams.reactor.util.SimpleSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FluxBackpressureTest {

    @Test
    @Tag("async")
    @Tag("explicit demand")
    @Tag("failure")
    @DisplayName("Shows how to explicitly request demand from hot source")
    void test01() {
        Flux.interval(Duration.ofSeconds(1))
                //.log()
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(5);
                    }

                    @Override
                    public void onNext(Long next) {
                        Util.printlnThread("Received: " + next);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Thrown: " + throwable);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Completed!");
                    }
                });
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("explicit demand")
    @DisplayName("Shows onBackpressureOverflow strategy with delayed demand and threads intercommunication")
    void test02() {
        Flux.interval(Duration.ofSeconds(1))
                .onBackpressureBuffer(2,
                        l -> System.out.println("Dropped: " + l),
                        BufferOverflowStrategy.DROP_OLDEST)
                //.log()
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(3);
                        //Schedule later demand
                        Schedulers.newParallel("myTimer").schedule(
                                () -> subscription.request(5), 8, TimeUnit.SECONDS);
                    }

                    @Override
                    public void onNext(Long next) {
                        Util.printlnThread("Received: " + next);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Thrown: " + throwable);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Completed!");
                    }
                });
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @DisplayName("Shows usage of caching wrapper over hot source")
    void test03() {
        Flux<Object> hotCachingSource = Flux.create(emitter -> {
            new SimpleAsyncMessageSource(msg -> {
                emitter.next(msg);
            });
        }).cache(3);
        System.out.println("When use Flux#create() nothing happens until you subscribe");
        hotCachingSource.subscribe(l -> Util.printlnThread("[Subscriber 1] " + l));
        Util.wait(5, SECONDS);
        hotCachingSource.subscribe(l -> Util.printlnThread("[Subscriber 2] " + l));
        Util.wait(5, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("explicit demand")
    @Tag("warning") //Because hot sources always require explicit backpressure policy in place. See #test10.
    @DisplayName("Shows that demand is controlled by parent composite publisher and propagated to underlying publishers")
    void test04() {
        Flux.interval(Duration.ofSeconds(1)) //First interval publisher
                        //.publishOn(Schedulers.newSingle("Timer1")) //Affects merged flux
                        //.log() //Initially request 32 items regardless of demand
                        //.doOnNext(Util::printlnThread)
                .mergeWith(Flux.interval(Duration.ofSeconds(1)) //Second interval publisher
                        //.doOnNext(Util::printlnThread)
                        .publishOn(Schedulers.newSingle("Timer2")) //PublishOn publisher
                        .log() //Initially request 32 items regardless of demand
                        //.doOnNext(Util::printlnThread) //Called only if element has been actually emitted
                        )
                //.log()
                .subscribe(new SimpleSubscriber<>("My subscriber", 5));
        Util.wait(10, SECONDS);
    }

}
