package org.reactivestreams.reactor.gems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FlatMapInsteadOfConcatMap {

    @Test
    @DisplayName("Shows how you can replace concatMap with flatMap even for async concurrent publishers")
    void test01() {
        Flux<Integer> source = Flux.range(0, 100);

        //By default flatMap allow concurrent processing of emitted elements
        source.flatMap(i -> Flux.just(i)
                    .doOnNext(j -> Util.wait(10, MILLISECONDS))
                    .subscribeOn(Schedulers.elastic()))
                .subscribe(System.out::println);

        //concatMap preserves ordering and concatenates emissions instead of merging (no interleave)
        /*source.concatMap(i -> Flux.just(i)
                    .doOnNext(j -> Util.wait(10, MILLISECONDS))
                    .subscribeOn(Schedulers.elastic()))
                .subscribe(System.out::println);*/

        //By setting concurrency to 1 you enforce similar behavior as concatMap
        /*source.flatMap(i -> Flux.just(i)
                    .doOnNext(j -> Util.wait(10, MILLISECONDS))
                    .subscribeOn(Schedulers.elastic()), 1)
                .subscribe(System.out::println);*/

        Util.wait(2, SECONDS);
    }
}
