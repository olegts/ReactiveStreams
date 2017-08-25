package org.reactivestreams.reactor.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.intergration.sink.SlowConcurrentConsumer;
import org.reactivestreams.reactor.intergration.source.HotIntSource;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class MultiThreadedPushTest {

    @Test
    @DisplayName("Shows parallel processing of messages from fast hot source to slow consumer using parallel flux")
    void test01() {

        SlowConcurrentConsumer<Integer> consumer = new SlowConcurrentConsumer<>();

        StepVerifier.create(
                Flux.<Integer>create(emitter -> {
                    new HotIntSource(msg -> emitter.next(msg));
                })
                        .onBackpressureBuffer(10, l -> {}/*Util.printlnThread("Drop: " + l)*/, BufferOverflowStrategy.DROP_OLDEST)
                        .parallel(8)
                        .runOn(Schedulers.newParallel("Parallel worker", 8))
                        .doOnNext(m -> consumer.receive(m))
                        .sequential()
                        .doOnNext(Util::printlnThread),
                100)
                .expectNextCount(100)
                //.then(() -> assertThat(consumer.getMessages().size(), equalTo(10))) //in fact consumer received more messages. do you know why?
                .thenCancel()
                .verify();
    }
}
