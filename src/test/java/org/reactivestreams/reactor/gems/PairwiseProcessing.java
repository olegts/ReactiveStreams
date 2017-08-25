package org.reactivestreams.reactor.gems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class PairwiseProcessing {

    @Test
    @DisplayName("Shows how to process elements pairwise")
    void test01() {
        Flux.range(1, 9)
                .publish(o -> Flux.zip(o, o.skip(1), (a, b) -> a + b))
                .subscribe(System.out::println);
    }

}
