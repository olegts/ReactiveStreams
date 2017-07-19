package org.reactivestreams.reactor.flux;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FluxThreadsTest {

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows simplest way how to execute flux on diff thread")
    void test01() {
        Flux.just(1, 2, 3)
                //.doOnNext(Util::printlnThread)
                .subscribeOn(Schedulers.single())
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows case where concurrent publishers being executed each by it's own thread")
    void test02() {
        Flux.just(1, 2, 3).subscribeOn(Schedulers.newSingle("thread1"))
                .mergeWith(Flux.just(4, 5, 6).subscribeOn(Schedulers.newSingle("thread2")))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows difference between subscribeOn and publishOn")
    void test03() {
        Flux.just(1, 2, 3)
                .doOnNext(Util::printlnThread)
                .publishOn(Schedulers.single())
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows both subscribeOn and publishOn usage")
    void test04() {
        Flux.just(1, 2, 3)
                .doOnNext(Util::printlnThread)
                .subscribeOn(Schedulers.newSingle("thread1")) //could be anywhere in chain because applied to the beginning anyway
                .publishOn(Schedulers.newSingle("thread2"))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows priority of native timer scheduler over custom scheduler")
    void test05() {
        Flux.interval(Duration.ofSeconds(1))
                .subscribeOn(Schedulers.newSingle("thread1"))
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows usage of timer based publisher with publishOn")
    void test06() {
        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(Util::printlnThread)
                .publishOn(Schedulers.newSingle("thread1"))
                .subscribe(Util::printlnThread);
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows that usage of parallel scheduler doesn't make stream parallel")
    void test07() {
        Flux.range(1, 1000)
                .subscribeOn(Schedulers.parallel()) //usage of parallel scheduler doesn't make stream parallel!!!
                .subscribe(Util::printlnThread);
        Util.wait(1, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows parallel processing of data using flatMap with parallel scheduler and some interesting Reactor optimization")
    void test08() {
        Flux.range(1, 1000)
                .flatMap(i -> Flux.just(i)
                        //.doOnNext(j -> Util.wait(10, MILLISECONDS))
                        .subscribeOn(Schedulers.parallel()))
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows parallel processing of data using flatMap with elastic scheduler")
    void test09() {
        Flux.range(1, 1000)
                .flatMap(i -> Flux.just(i)
                        //.doOnNext(j -> Util.wait(10, MILLISECONDS))
                        .subscribeOn(Schedulers.elastic()))
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows native Reactor parallel stream processing")
    void test10() {
        Flux.range(1, 1000)
                .parallel(8) //tells Reactor how many parallel rails to create with regard of splitting original stream
                .runOn(Schedulers.newParallel("myThread", 4)) //specifies how many threads will actually do the job
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows native Reactor parallel stream processing with defaults")
    void test11() {
        Flux.range(1, 100)
                .parallel()
                .runOn(Schedulers.newParallel("mythread"))
                .doOnNext(i -> Util.wait(10, MILLISECONDS)) //emulating long running operation
                .sequential() //go back to normal Flux but not introducing new threads!
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }
}
