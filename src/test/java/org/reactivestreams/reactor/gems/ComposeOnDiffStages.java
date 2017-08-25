package org.reactivestreams.reactor.gems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;

import static reactor.core.publisher.Flux.defer;
import static reactor.core.publisher.Flux.range;

public class ComposeOnDiffStages {

    @Test
    @DisplayName("Shows compose operation at assembly time")
    void test01() {
        Function<Publisher<Integer>, Publisher<Integer>> addSchedulers = o ->
                ((Flux<Integer>) o).subscribeOn(Schedulers.elastic());

        range(1, 10).compose(addSchedulers).subscribe(System.out::println);
    }

    @Test
    @DisplayName("Shows compose operation at subscription time")
    void test02() {
        Function<Publisher<Integer>, Publisher<Integer>> addSchedulers = o ->
                ((Flux<Integer>) o).subscribeOn(Schedulers.elastic());

        range(1, 10).compose(o -> defer(() -> {
            int[] counter = new int[]{0};
            return o.map(v -> ++counter[0]);
        })).subscribe(System.out::println);
    }
}
