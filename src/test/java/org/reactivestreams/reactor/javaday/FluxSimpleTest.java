package org.reactivestreams.reactor.javaday;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.util.SimpleAsyncMessageSource;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FluxSimpleTest {

    @Test
    @Tag("sync")
    @Tag("bounded demand")
    @DisplayName("Shows simplest demand control")
    void test01() {
        Flux.just(1, 2, 3)
                //.log()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("Received: " + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Exception occured: " + throwable);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Completed!");
                    }
                });
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows difference of collect operator from classic Streams")
    void test02() {
        /*Flux.just(1, 2, 3)
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .collectList() //Returns Mono<List<Integer>> instead of simple List<Integer>
                .subscribe(System.out::println);*/

        Flux.interval(Duration.ofSeconds(1))
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .collectList() //Returns Mono<List<Integer>> instead of simple List<Integer>
                .subscribe(System.out::println); //Mono value never gets emitted, because initial Flux is infinite
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @Tag("blocking")
    @DisplayName("Shows blocking transformation from Flux to Stream")
    void test03() {
        Flux.just(1, 2, 3)
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .toStream() //blocking operation
                .forEach(System.out::println);

        /*long count = Flux.interval(Duration.ofSeconds(1))
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .toStream() //blocking operation
                .count();
        System.out.println(count);*/
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows possibility to create Flux from generator")
    void test04() {
        AtomicInteger counter = new AtomicInteger(0);
        Flux.generate(sink -> {
            if (counter.get() < 100) {
                sink.next(counter.getAndIncrement());
            } else {
                sink.complete();
            }
        }).subscribe(System.out::println);
    }

    @Test
    @Tag("async")
    @Tag("bounded emission")
    @DisplayName("Shows async finite Flux based on simple Timer")
    void test05() {
        Flux.interval(Duration.ofSeconds(1))
                .take(5)
                .log()
                .subscribe(System.out::println);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("bounded demand")
    @DisplayName("Shows how to create Flux from existing async hot source")
    void test06() {
        Flux.create(emitter -> {
            new SimpleAsyncMessageSource(msg -> {
                emitter.next(msg);
            });
        })
                //.log()
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(5);
                    }

                    @Override
                    public void onNext(Object o) {
                        Util.printlnThread("Received: " + o);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Exception occured: " + throwable);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Completed!");
                    }
                });
        Util.wait(10, SECONDS);
    }
}
