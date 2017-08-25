package org.reactivestreams.reactor.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.intergration.sink.FastConsumer;
import org.reactivestreams.reactor.intergration.sink.SlowConsumer;
import org.reactivestreams.reactor.intergration.source.HotIntSource;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SingleThreadedPushTest {

    @Test
    @DisplayName("Synchronously push messages from hot source to consumer using buffer")
    void test01() {

        FastConsumer<Integer> consumer = new FastConsumer<>();

        StepVerifier.create(
                Flux.<Integer>create(emitter -> {
                    new HotIntSource(msg -> emitter.next(msg));
                })
                        .onBackpressureBuffer(10, l -> Util.printlnThread("Drop: " + l), BufferOverflowStrategy.DROP_OLDEST)
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
        10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(10)))
                .thenRequest(10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(20)))
                .thenCancel()
                .verify();

    }

    @Test
    @DisplayName("Asynchronously push messages from hot source to consumer using buffer")
    void test02() {

        SlowConsumer<Integer> consumer = new SlowConsumer<>();

        StepVerifier.create(
                Flux.<Integer>create(emitter -> {
                    new HotIntSource(msg -> emitter.next(msg));
                })
                        //.subscribeOn(Schedulers.newSingle("Daemon thread")) //Ignored
                        .onBackpressureBuffer(10, l -> {}/*Util.printlnThread("Drop: " + l)*/, BufferOverflowStrategy.DROP_OLDEST)
                        .publishOn(Schedulers.newSingle("Daemon thread")) //Works
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
                10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(10)))
                .thenRequest(10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(20)))
                .thenCancel()
                .verify();

    }
}
