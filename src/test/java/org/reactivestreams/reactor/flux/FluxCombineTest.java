package org.reactivestreams.reactor.flux;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FluxCombineTest {

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows how to subscribe on flux completion to be notified when flux completed")
    void test01() {
        Mono<Void> completion = Flux.just(1, 2, 3)
                .delaySubscription(Duration.ofSeconds(5))
                .then();
        completion.subscribe(t -> {}, ex -> {}, () -> System.out.println("Flux completed!"));
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows how to wait sequential completion of two flux")
    void test02() {
        Mono<Void> completion = Flux.just(1, 2, 3)
                .thenEmpty(Flux.<Void>empty()
                        .delaySubscription(Duration.ofSeconds(5)));
        completion.subscribe(t -> {}, ex -> {}, () -> System.out.println("Flux completed!"));
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows how to subscribe for second flux once first flux completed")
    void test03() {
        Flux.just(1, 2, 3)
                .thenMany(Flux.just(4, 5, 6))
                .subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows how to concat two streams sequentially into one stream")
    void test04() {
        Flux.just(1, 2, 3)
                .concatWith(Flux.just(4, 5, 6))
                .subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows how to sequentially concat multiple streams generated based on elements of original stream")
    void test05() {
        Flux.just(1, 2, 3)
                .concatMap(i -> Flux.just(i + "1", i + "2", i + "3"))
                .subscribe(System.out::println);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows how to sequentially concat multiple streams generated based on elements of original stream asynchronously")
    void test06() {
        Flux.just(1, 2, 3)
                .concatMap(i -> Flux.just(i + "1", i + "2", i + "3")
                        .subscribeOn(Schedulers.newSingle("My" + i)))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows ability to switch to fall back stream in case original one is empty")
    void test07() {
        Flux.empty()
                .switchIfEmpty(Flux.just(1, 2, 3))
                .subscribe(System.out::println);

        Flux.just(1, 2, 3)
                .switchIfEmpty(Flux.just(4, 5, 6))
                .subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Analog of concatMap method")
    void test08() {
        Flux.just(1, 2, 3)
                .switchMap(i -> Flux.just(i + "1", i + "2", i + "3"))
                .subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows ability to provide fallback stream in case original emits exception")
    void test09() {
        Flux.just(1, 2, 3)
                .handle((i, sink) -> {
                    if (i == 3) {
                        sink.error(new RuntimeException("Shit happens"));
                    } else {
                        sink.next(i);
                    }
                })
                .onErrorResume(ex -> Flux.just(4, 5, 6))
                .subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows synchronous merge of 2 streams")
    void test10() {
        Flux.just(1, 2, 3)
                .mergeWith(Flux.just(4, 5, 6))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows asynchronous merge of 2 streams")
    void test11() {
        Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .mergeWith(Flux.range(-10, 10)
                        .delayElements(Duration.ofSeconds(1))
                        .publishOn(Schedulers.newSingle("New thread"))
                 )
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows effect of synchronous zip operation")
    void test12() {
        Flux.range(0, 10)
                .zipWith(Flux.just("a", "b", "c"))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows effect of asynchronous zip operation")
    void test13() {
        Flux.range(0, 10)
                .zipWith(Flux.interval(Duration.ofSeconds(1)))
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }
}
