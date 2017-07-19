package org.reactivestreams.reactor.flux;

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
    @Tag("unbounded demand")
    @DisplayName("Simplest flux creation from sequence of elements")
    void test01() {
        Flux.just(1, 2, 3)
                .log()
                .subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Convenient flux creation from Iterable, Array or Stream")
    void test02() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        Flux
                .fromIterable(list)
                //.fromStream(list.stream())
                //.fromArray(list.toArray())
                .log()
                .subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows behavior of empty Flux")
    void test03() {
        Flux.empty()
                //.log()
                .subscribe(
                        System.out::println,
                        System.err::println,
                        () -> System.out.println("Flux completed!"))
        ;
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows consequences of exception thrown on Flux")
    void test04() {
        Flux.error(new RuntimeException("Oops!!!"))
                //.log()
                .subscribe(
                        System.out::println,
                        exception -> System.out.println("Exception occured: " + exception)
                        //exception -> {/*DO NOTHING*/}
                );
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows usage of Subscriber")
    void test05() {
        Flux.just(1, 2, 3)
                //.log()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
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
    @Tag("bounded demand")
    @DisplayName("Shows simplest demand control")
    void test06() {
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
    @DisplayName("Shows analogy with classic Streams reg operators chaining")
    void test07() {
        Flux.just(1, 2, 3)
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .log()
                .subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows difference of collect operator from classic Streams")
    void test08() {
        Flux.just(1, 2, 3)
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .collectList() //Returns Mono<List<Integer>> instead of simple List<Integer>
                .subscribe(System.out::println);

        /*Flux.interval(Duration.ofSeconds(1))
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .collectList() //Returns Mono<List<Integer>> instead of simple List<Integer>
                .subscribe(System.out::println); //Mono value never gets emitted, because initial Flux is infinite
        Util.wait(10, SECONDS);*/
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @Tag("blocking")
    @DisplayName("Shows blocking transformation from Flux to Stream")
    void test09() {
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
    @Disabled
    @Tag("sync")
    @Tag("unbounded demand")
    @Tag("infinite")
    @DisplayName("Shows possibility to create Flux from infinite generator")
    void test10() {
        AtomicInteger counter = new AtomicInteger(0);
        Flux.generate(sink -> {
            sink.next(counter.getAndIncrement());
        }).subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows possibility to create Flux from finite generator")
    void test11() {
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
    @Tag("unbounded demand")
    @Tag("infinite")
    @DisplayName("Shows async infinite Flux based on simple Timer")
    void test12() {
        Flux.interval(Duration.ofSeconds(1))
                .subscribe(Util::printlnThread)
        ;
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("bounded emission")
    @DisplayName("Shows async finite Flux based on simple Timer")
    void test13() {
        Flux.interval(Duration.ofSeconds(1))
                .take(5)
                .log()
                .subscribe(System.out::println);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @Tag("infinite")
    @DisplayName("Shows how to create Flux from existing async hot source")
    void test14() {
        Flux.create(emitter -> {
            new SimpleAsyncMessageSource(msg -> {
                emitter.next(msg);
            });
        })
                //.log()
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("bounded demand")
    @DisplayName("Shows how to create Flux from existing async hot source")
    void test15() {
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
