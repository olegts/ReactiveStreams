package org.reactivestreams.reactor.javaday;

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
    @DisplayName("Shows how to sequentially concat multiple streams generated based on elements of original stream asynchronously")
    void test01() {
        Flux.just(1, 2, 3)
                .concatMap(i -> Flux.just(i + "1", i + "2", i + "3")
                        .subscribeOn(Schedulers.newSingle("My" + i)))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows ability to provide fallback stream in case original emits exception")
    void test02() {
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
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows asynchronous merge of 2 streams")
    void test03() {
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
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows effect of asynchronous zip operation")
    void test04() {
        Flux.range(0, 10)
                .zipWith(Flux.interval(Duration.ofSeconds(1)))
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }
}
