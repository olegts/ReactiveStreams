package org.reactivestreams.reactor.gems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ParallelProcessing {

    @Test
    @Tag("Bug: 'publishOn' doesn't work as expected if placed not at the end of flux creation chain")
    @DisplayName("Shows evenly distributed processing with help of 'groupBy' operator")
    void test01() {
        Scheduler scheduler = Schedulers.newParallel("custom", 8);
        Flux.range(1, 1_000)
                .groupBy(v -> v.hashCode() % 8)
                .flatMap(g -> g.subscribeOn(scheduler)
                        .doOnNext(j -> Util.wait(10, MILLISECONDS))
                        .map(Object::hashCode))
                .subscribe(Util::printlnThread);

        //doesn't work as expected
        /*Flux.range(1, 1_000)
                .groupBy(v -> v.hashCode() % 8)
                .flatMap(g -> g.publishOn(scheduler)
                        .doOnNext(j -> Util.wait(10, MILLISECONDS))
                        .map(Object::hashCode))
                .subscribe(Util::printlnThread);*/

        Util.wait(2, SECONDS);
    }
}
