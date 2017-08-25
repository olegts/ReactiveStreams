package org.reactivestreams.reactor;

import org.jooq.lambda.Unchecked;

import java.util.concurrent.TimeUnit;

public class Util {

    public static void wait(int duration, TimeUnit timeUnit) {
        Unchecked.runnable(() -> Thread.sleep(timeUnit.toMillis(duration))).run();
    }

    public static void printlnThread(Object msg) {
        System.out.println("[" + Thread.currentThread() + "] " + msg);
    }
}
