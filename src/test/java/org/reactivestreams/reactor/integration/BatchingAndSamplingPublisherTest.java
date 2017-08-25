package org.reactivestreams.reactor.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.intergration.sink.Consumer;
import org.reactivestreams.reactor.intergration.sink.FastConsumer;
import org.reactivestreams.reactor.intergration.sink.SlowConsumer;
import org.reactivestreams.reactor.intergration.sink.ThrottledConsumer;
import org.reactivestreams.reactor.intergration.source.HotIntSource;
import org.reactivestreams.reactor.intergration.source.SlowHotIntSource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BatchingAndSamplingPublisherTest {

    @Test
    @DisplayName("Shows ability to create batches of sampled events from fast source to throttled consumer")
    void test01() {

        Consumer<List<Integer>> consumer = new ThrottledConsumer<>(10);

        //Work fine
        StepVerifier.create(
                Flux.<Integer>create(emitter -> {
                    new HotIntSource(msg -> emitter.next(msg));
                })
                        .buffer(Duration.of(1, ChronoUnit.MILLIS))
                        .onBackpressureDrop()
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
                10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(10)))
                .thenCancel()
                .verify();

        //Shows exception
        /*StepVerifier.create(
                Flux.<Integer>create(emitter -> {
                    new HotIntSource(msg -> emitter.next(msg));
                })
                        .buffer(Duration.of(1, ChronoUnit.MILLIS))
                        .onBackpressureDrop()
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
                11)
                .expectNextCount(11)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(11)))
                .thenCancel()
                .verify();*/
    }

    @Test
    @DisplayName("Shows ability to create batches of sampled events with certain throttling rate")
    void test02() {

        Consumer<List<Integer>> consumer = new ThrottledConsumer<>(10);

        //Shows exception
        StepVerifier.create(
                Flux.<Integer>create(emitter -> {
                    new SlowHotIntSource(msg -> emitter.next(msg));
                })
                        .buffer(Duration.of(200, ChronoUnit.MILLIS))
                        .onBackpressureDrop()
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
                20)
                .expectNextCount(20)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(20)))
                .thenCancel()
                .verify();
    }
}
