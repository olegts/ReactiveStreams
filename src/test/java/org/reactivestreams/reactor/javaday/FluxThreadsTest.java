package org.reactivestreams.reactor.javaday;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FluxThreadsTest {

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows case where concurrent publishers being executed each by it's own thread")
    void test01() {
        Flux.just(1, 2, 3).subscribeOn(Schedulers.newSingle("thread1"))
                .mergeWith(Flux.just(4, 5, 6).subscribeOn(Schedulers.newSingle("thread2")))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows both subscribeOn and publishOn usage")
    void test02() {
        Flux.just(1, 2, 3)
                .doOnNext(Util::printlnThread)
                .subscribeOn(Schedulers.newSingle("thread1")) //could be anywhere in chain because applied to the beginning anyway
                .publishOn(Schedulers.newSingle("thread2"))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows that usage of parallel scheduler doesn't make stream parallel")
    void test03() {
        Flux.range(1, 1000)
                .subscribeOn(Schedulers.parallel()) //usage of parallel scheduler doesn't make stream parallel!!!
                .subscribe(Util::printlnThread);
        Util.wait(1, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows parallel processing of data using flatMap with parallel and elastic schedulers and some interesting Reactor optimization")
    void test04() {
        Flux.range(1, 1000)
                .flatMap(i -> Flux.just(i)
                        .doOnNext(j -> Util.wait(10, MILLISECONDS))
                        //.subscribeOn(Schedulers.parallel()))
                        .subscribeOn(Schedulers.elastic()))
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }

    @Test
    @Tag("async")
    @Tag("unbounded demand")
    @DisplayName("Shows native Reactor parallel stream processing")
    void test05() {
        Flux.range(1, 1000)
                .parallel(8) //tells Reactor how many parallel rails to create with regard of splitting original stream
                .runOn(Schedulers.newParallel("myThread", 4)) //specifies how many threads will actually do the job
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }
}
