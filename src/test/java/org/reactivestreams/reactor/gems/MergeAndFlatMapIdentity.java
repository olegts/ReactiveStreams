package org.reactivestreams.reactor.gems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Function;

import static reactor.core.publisher.Flux.merge;

public class MergeAndFlatMapIdentity {

    @Test
    @DisplayName("Shows interchangeability of merge and flatMap operations in certain cases")
    void test01() {
        Publisher<Publisher<Integer>> nestedPublisher = Flux.just(Flux.just(1), Flux.just(2), Flux.just(3));

        System.out.println("Result of merging several Publishers:");
        merge(nestedPublisher).subscribe(System.out::println);

        System.out.println("Result of flatMap operation on nested Publishers:");
        ((Flux<Publisher<Integer>>) nestedPublisher)
                .flatMap(p -> p)
                .subscribe(System.out::println);
    }

    @Test
    @DisplayName("Shows interchangeability of merge and flatMap operations in certain cases")
    void test02() {
        Flux<Integer> source = Flux.just(1, 2, 3);
        Function<Integer, Publisher<Integer>> mapper = i -> Flux.just(i);

        System.out.println("Result of merging several Publishers:");
        merge(source.map(mapper)).subscribe(System.out::println);

        System.out.println("Result of flatMap operation on nested Publishers:");
        source.flatMap(mapper).subscribe(System.out::println);
    }
}
