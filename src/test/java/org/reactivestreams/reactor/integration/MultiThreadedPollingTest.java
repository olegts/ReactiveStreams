package org.reactivestreams.reactor.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.intergration.sink.SlowConcurrentConsumer;
import org.reactivestreams.reactor.intergration.source.PollingIntSource;
import org.reactivestreams.reactor.intergration.source.PollingSource;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class MultiThreadedPollingTest {

    @Test
    @DisplayName("Shows parallel processing of messages from fast polling source to slow consumer using flatMap")
    void test01() {

        SlowConcurrentConsumer<Integer> consumer = new SlowConcurrentConsumer<>();
        PollingSource<Integer> source = new PollingIntSource();

        StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .subscribeOn(Schedulers.newSingle("Polling thread"))
                        .doOnNext(Util::printlnThread)
                        .flatMap(msg -> Flux.just(msg)
                                .doOnNext(m -> consumer.receive(m)) //Should be before #subscribeOn in order to be performed by elastic threads in parallel
                                //.subscribeOn(Schedulers.elastic()))
                                .subscribeOn(Schedulers.parallel()))
                        .doOnNext(Util::printlnThread),
                100)
                .expectNextCount(100)
                //.then(() -> assertThat(consumer.getMessages().size(), equalTo(100))) //in fact consumer received more messages. why?
                .thenCancel()
                .verify();
    }

    @Test
    @DisplayName("Shows parallel processing of messages from fast polling source to slow consumer using parallel flux")
    void test02() {

        SlowConcurrentConsumer<Integer> consumer = new SlowConcurrentConsumer<>();
        PollingSource<Integer> source = new PollingIntSource();

        StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .subscribeOn(Schedulers.newSingle("Polling thread"))
                        .doOnNext(Util::printlnThread)
                        .parallel(8)
                        .runOn(Schedulers.newParallel("Parallel worker", 8))
                        //.doOnNext(Util::printlnThread) //You can see round-robin of threads output in logs
                        .doOnNext(m -> consumer.receive(m))
                        .sequential()
                        .doOnNext(Util::printlnThread), //You can see grouped output by threads in logs. can you explain it?
                100)
                .expectNextCount(100)
                //.then(() -> assertThat(consumer.getMessages().size(), equalTo(100))) //in fact consumer received more messages. do you know why?
                .thenCancel()
                .verify();
    }
}
