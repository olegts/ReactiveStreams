package org.reactivestreams.reactor;

import org.jooq.lambda.Unchecked;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalTime;

public class MonoTest {

    @Test
    void simpleCreate() {

        Mono<String> mono = Mono.just("Result");

    }

    @Test
    void monoOrMethod() throws InterruptedException {
        Mono.delayMillis(2000)
                .map(l -> "Spring 4")
                .or(Mono.delayMillis(1000).map(l -> "Spring 5"))
                .subscribe(System.out::println);
        Thread.sleep(3000);
    }

    @Test
    void monoOrMethodShowingThreads() throws InterruptedException {
        Mono.delayMillis(2000, Schedulers.newTimer("spring4"))
                .map(l -> "Spring 4")
                .or(Mono.delayMillis(1000, Schedulers.newTimer("spring5")).map(l -> "Spring 5"))
                .subscribe(s -> {
                    System.out.println("[" + Thread.currentThread() + "] " + s);
                });
        Thread.sleep(3000);
    }

    @Test
    void monoThenOtherMethod() throws InterruptedException {
        Mono.delayMillis(1000)
                .then(Mono.just("Other result"))
                .subscribe(System.out::println);
        Thread.sleep(3000);
    }

    @Test
    void monoThenTransformMethod() throws InterruptedException {
        Mono.delayMillis(1000)
                .then(l -> Mono.just("Other result then " + l))
                .subscribe(System.out::println);
        Thread.sleep(2000);
    }

    @Test
    void monoThenOtherMethodShowingThreads() throws InterruptedException {
        System.out.println(LocalTime.now());
        Mono.delayMillis(1000)
                .doOnNext(l -> {
                    System.out.println("[" + Thread.currentThread() + "] " + l);
                    System.out.println(LocalTime.now());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .then(Mono.just("Other result").doOnNext(s -> {
                    System.out.println("[" + Thread.currentThread() + "] " + s);
                    System.out.println(LocalTime.now());
                }))
                .subscribe(System.out::println);
        Thread.sleep(6000);
    }

    @Test
    void monoElapsed() throws InterruptedException {
        Mono.delayMillis(1000)
                .elapsed()
                .subscribe(System.out::println);
        Thread.sleep(2000);
    }

    @Test
    void monoAndOtherMono() {
        Mono.just(1)
                .and(Mono.just("One"))
                .subscribe(System.out::println);
    }

    @Test
    void monoAndOtherMonoWithDelay() {
        Mono.delay(Duration.ofSeconds(5))
                .and(Mono.just("One"))
                .subscribe(System.out::println);
        Unchecked.runnable(() -> Thread.sleep(10000)).run();
    }

    @Test
    void monoAndEmptyMono() {
        Mono.just(1)
                .and(Mono.empty())
                .subscribe(System.out::println, System.out::println, () -> System.out.println("Completed"));
        Unchecked.runnable(() -> Thread.sleep(10000)).run();
    }

    @Test
    void monoAndErrorMono() {
        Mono.just(1)
                .and(Mono.error(new RuntimeException("Exception")))
                .subscribe(System.out::println, System.out::println, () -> System.out.println("Completed"));
        Unchecked.runnable(() -> Thread.sleep(10000)).run();
    }

    @Test
    void monoAndNeverMono() {
        Mono.just(1)
                .and(Mono.never())
                .subscribe(System.out::println, System.out::println, () -> System.out.println("Completed"));
        Unchecked.runnable(() -> Thread.sleep(10000)).run();
    }

    @Test
    void whenBothMonos(){
        Mono.when(
                Mono.just(1),
                Mono.just("One")
        ).subscribe(System.out::println);
    }

    @Test
    void combineTwoMonos(){
        Mono.when(Mono.just(1),
                Mono.just("One"),
                (i,s) -> s+i)
        .subscribe(System.out::println);
    }
}
