package org.reactivestreams.reactor.gems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.function.Predicate;

import static reactor.core.publisher.Flux.empty;
import static reactor.core.publisher.Flux.just;

public class FlatMapInsteadOfMapAndFilter {

    @Test
    @DisplayName("Shows how to use 'flatMap' operation instead of 'map' if required")
    void test01() {
        Flux<Integer> source = just(1, 2, 3);
        Function<Integer, String> mapper = String::valueOf;

        System.out.println("Using simple 'map' operation:");
        source.map(mapper).subscribe(System.out::println);

        System.out.println("Using 'flatMap' operation:");
        source.flatMap(e -> just(mapper.apply(e))).subscribe(System.out::println);
    }

    @Test
    @DisplayName("Shows how to use 'flatMap' operation instead of 'filter' if required")
    void test02() {
        Flux<Integer> source = just(1, 2, 3);
        Predicate<Integer> predicate = e -> e % 2 == 0;

        System.out.println("Using simple 'filter' operation:");
        source.filter(predicate).subscribe(System.out::println);

        System.out.println("Using 'flatMap' operation:");
        source.flatMap(e -> predicate.test(e) ? just(e) : empty()).subscribe(System.out::println);
    }

}
