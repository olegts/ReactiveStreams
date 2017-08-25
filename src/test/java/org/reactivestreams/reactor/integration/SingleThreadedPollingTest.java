package org.reactivestreams.reactor.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.intergration.sink.FastConsumer;
import org.reactivestreams.reactor.intergration.sink.SlowConsumer;
import org.reactivestreams.reactor.intergration.source.PollingIntSource;
import org.reactivestreams.reactor.intergration.source.SlowPollingIntSource;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SingleThreadedPollingTest {

    @Test
    @DisplayName("Synchronous data exchange between fast producer and fast consumer")
    void test01() {
        FastConsumer<Integer> consumer = new FastConsumer<>();
        PollingIntSource source = new PollingIntSource();

        StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
        5)
                .expectNextCount(5)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(5)))
                .thenCancel()
                .verify();

    }

    @Test
    @DisplayName("Synchronous data exchange between fast producer and slow consumer")
    void test02() {
        SlowConsumer<Integer> consumer = new SlowConsumer<>();
        PollingIntSource source = new PollingIntSource();

        StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .subscribeOn(Schedulers.newSingle("Daemon thread"))
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
        5)
                .expectNextCount(5)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(5)))
                .thenCancel()
                .verify();

    }

    @Test
    @DisplayName("Synchronous data exchange between fast consumer and slow consumer")
    void test03() {
        FastConsumer<Integer> consumer = new FastConsumer<>();
        SlowPollingIntSource source = new SlowPollingIntSource();

        StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .subscribeOn(Schedulers.newSingle("Daemon thread"))
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
        5)
                .expectNextCount(5)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(5)))
                .thenCancel()
                .verify();

    }

    @Test
    @DisplayName("Synchronous data exchange between slow consumer and slow consumer")
    void test04() {
        SlowConsumer<Integer> consumer = new SlowConsumer<>();
        SlowPollingIntSource source = new SlowPollingIntSource();

        StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .subscribeOn(Schedulers.newSingle("Daemon thread"))
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
        5)
                .expectNextCount(5)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(5)))
                .thenCancel()
                .verify();

    }
}
