package org.reactivestreams.reactor.javaday;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FluxRetryTest {

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows effect of limited retry operator on Flux")
    void test01() {
        final Random random = new Random();
        Flux.range(1, 10)
                .handle((i, sink) -> {
                    if (random.nextBoolean()) {
                        throw new RuntimeException("Sparadic exception");
                    } else {
                        sink.next(i);
                    }
                })
                //.log()
                .retry(3) //retries stream consumption from the beginning
                .subscribe(Util::printlnThread,
                        ex -> System.out.println("Exception occured. Giving up."),
                        () -> System.out.println("Completed!"));
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows effect of retry operator based on matcher")
    void test02() {
        final Random random = new Random();
        Flux.range(1, 10)
                .handle((i, sink) -> {
                    if (i == 10) {
                        throw new NullPointerException("Fatal exception");
                    } else if (random.nextBoolean()) {
                        sink.error(new IOException("Retryble exception"));
                    } else {
                        sink.next(i);
                    }
                })
                //.log()
                .retry(ex -> ex instanceof IOException) //retries stream consumption from the beginning
                .subscribe(Util::printlnThread,
                        ex -> System.out.println("Exception occured. Giving up." + ex),
                        () -> System.out.println("Completed!"));
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows effect of finite repeat operator")
    void test03() {
        Flux.just(1, 2, 3)
                //.log()
                .repeat(2)
                .subscribe(Util::printlnThread,
                        System.out::println,
                        () -> System.out.println("Completed!"));
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Shows that repeat operator is not get applied in case of exception")
    void test04() {
        Flux.just(1, 2, 3).concatWith(Flux.error(new RuntimeException("Error occured")))
                //.log()
                .repeat()
                .subscribe(Util::printlnThread,
                        System.out::println,
                        () -> System.out.println("Completed!"));
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows ability to cache only certain number of messages to replay them upon new subscription")
    void test05() {
        ConnectableFlux<Long> connectableFlux = Flux.interval(Duration.ofSeconds(1))
                .replay(3);
        connectableFlux.subscribe(i -> Util.printlnThread("First subscriber received: " + i));
        System.out.println("Nothing yet happened at this point!!!");
        connectableFlux.connect();
        Util.wait(5, SECONDS);
        connectableFlux.subscribe(i -> Util.printlnThread("Second subscriber received: " + i));
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows ability to cache signals/messages from hot source for certain period of time")
    void test06() {
        ConnectableFlux<Long> connectableFlux = Flux.interval(Duration.ofSeconds(1))
                .replay(Duration.ofSeconds(2));
        connectableFlux.subscribe(i -> Util.printlnThread("First subscriber received: " + i));
        System.out.println("Nothing yet happened at this point!!!");
        connectableFlux.connect();
        Util.wait(5, SECONDS);
        connectableFlux.subscribe(i -> Util.printlnThread("Second subscriber received: " + i));
        Util.wait(10, SECONDS);
    }
}
