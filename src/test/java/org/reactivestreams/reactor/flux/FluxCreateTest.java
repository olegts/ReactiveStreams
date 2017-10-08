package org.reactivestreams.reactor.flux;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.util.SingleSubscriptionSimplePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FluxCreateTest {

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Create flux for range of values")
    void test01() {
        Flux.range(10, 10)
                .subscribe(System.out::println);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Create flux for range of values emitting them with delay")
    void test02() {
        Flux.range(10, 10)
                .delayElements(Duration.ofSeconds(1))
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Create flux for range of values with subscription delay")
    void test03() {
        Flux.range(10, 10)
                .delaySubscription(Duration.ofSeconds(5))
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Create flux for range of values with emission triggered by another Publisher")
    void test04() {
        Flux.range(10, 10)
                .delaySubscription(
                        Mono.empty().delaySubscription(Duration.ofSeconds(5)))
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @Tag("infinite")
    @DisplayName("Create flux based on generator with shared state passed between iterations")
    void test05() {
        Flux.generate(() -> 0, (i, sink) -> {
            sink.next(i);
            return ++i;
        }).subscribe(System.out::println);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Create defered flux which defers data polling from underlying 'possibly blocking' data source")
    void test06() {
        Flux<Integer> flux = Flux.defer(() -> Flux.fromIterable(getDataFromBlockingSource()));
        System.out.println("At this point no data has been requested!");
        flux.subscribe(Util::printlnThread);
    }

    List<Integer> getDataFromBlockingSource() {
        System.out.println("Called lazily only upon subscription!!!");
        return Arrays.asList(1, 2, 3);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @Tag("infinite")
    @DisplayName("Create flux based on existing Publisher")
    void test07() {
        Flux.from(new SingleSubscriptionSimplePublisher())
                .subscribe(System.out::println);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("sync")
    @Tag("unbounded demand")
    @DisplayName("Create flux which uses OS resources as source of data that should be released after consumption")
    void test08() {
        Flux<String> flux = Flux.using(
                () -> new Scanner(this.getClass().getClassLoader().getResourceAsStream("using.txt")), //Resource supplier
                scanner -> Flux.generate(sink -> { //Creation of publisher based on provided resource
                    if (scanner.hasNext()) {
                        sink.next(scanner.next());
                    } else {
                        sink.complete();
                    }
                }),
                scanner -> scanner.close()); //Resource finalizer
        System.out.println("At this point resources hasn't yet been requested/allocated!");
        flux.subscribe(System.out::println);
    }
}
