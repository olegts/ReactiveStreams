package org.reactivestreams.reactor.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.reactor.Util;
import org.reactivestreams.reactor.intergration.sink.Consumer;
import org.reactivestreams.reactor.intergration.sink.FastConsumer;
import org.reactivestreams.reactor.intergration.sink.FaultyConsumer;
import org.reactivestreams.reactor.intergration.source.FaultyPollingIntSource;
import org.reactivestreams.reactor.intergration.source.HotIntSource;
import org.reactivestreams.reactor.intergration.source.PollingIntSource;
import org.reactivestreams.reactor.intergration.source.PollingSource;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FaultyConsumerTest {

    @Test
    @DisplayName("Cold source with faulty consumer")
    void test01() {

        PollingSource<Integer> source = new PollingIntSource();
        Consumer<Integer> consumer = new FaultyConsumer<>();

        //Approach 1
        /*StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .subscribeOn(Schedulers.newSingle("Daemon thread"))
                        .doOnNext(msg -> {
                            while (true) {
                                try {
                                    consumer.receive(msg);
                                    //Util.wait(1, TimeUnit.MILLISECONDS);
                                    break;
                                } catch (Exception ex) {
                                    Util.printlnThread("Retrying " + msg);
                                }
                            }
                        })
                        .doOnNext(Util::printlnThread),
                10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(10)))
                .thenCancel()
                .verify();*/

        //Approach 2
        StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .subscribeOn(Schedulers.newSingle("Daemon thread"))
                        .flatMap(msg -> Mono.just(msg)
                                .doOnNext(message -> consumer.receive(message))
                                .retryWhen(companion -> companion))
                        .doOnNext(Util::printlnThread),
                10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(10)))
                .thenCancel()
                .verify();

    }

    @Test
    @DisplayName("Faulty cold source with fast consumer")
    void test02() {

        PollingSource<Integer> source = new FaultyPollingIntSource();
        Consumer<Integer> consumer = new FastConsumer<>();

        //Approch 1
        /*StepVerifier.create(
                Flux.<Integer>generate(sink -> {
                    while(true) {
                        try {
                            sink.next(source.poll());
                            break;
                        }catch(Exception ex){}
                    }
                })
                        .subscribeOn(Schedulers.newSingle("Daemon thread"))
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
                10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(10)))
                .thenCancel()
                .verify();*/

        //Approch 2
        StepVerifier.create(
                Flux.<Integer>generate(sink -> sink.next(source.poll()))
                        .retry()
                        .subscribeOn(Schedulers.newSingle("Daemon thread"))
                        .doOnNext(msg -> consumer.receive(msg))
                        .doOnNext(Util::printlnThread),
                10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(10)))
                .thenCancel()
                .verify();

    }

    @Test
    @DisplayName("Hot source with faulty consumer")
    void test03() {

        Consumer<Integer> consumer = new FaultyConsumer<>();

        StepVerifier.create(
                Flux.<Integer>create(emitter -> {
                    new HotIntSource(msg -> emitter.next(msg));
                })
                        .onBackpressureBuffer(256, l -> Util.printlnThread("Drop: " + l), BufferOverflowStrategy.DROP_OLDEST)
                        .publishOn(Schedulers.newSingle("Daemon thread")) //Works
                        .doOnNext(msg -> {
                            while (true) {
                                try {
                                    consumer.receive(msg);
                                    //Util.wait(1, TimeUnit.MILLISECONDS);
                                    break;
                                } catch (Exception ex) {
                                    Util.printlnThread("Retrying " + msg);
                                }
                            }
                        })
                        .doOnNext(Util::printlnThread),
                10)
                .expectNextCount(10)
                .then(() -> assertThat(consumer.getMessages().size(), equalTo(10)))
                .thenCancel()
                .verify();

    }
}
