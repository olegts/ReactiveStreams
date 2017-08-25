package org.reactivestreams.reactor.intergration.source;

import org.reactivestreams.reactor.Util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Arrays.asList;

public abstract class AbstractHotSource<T> implements HotSource<T>, Runnable {

    protected List<Listener<T>> listeners = new CopyOnWriteArrayList<>();

    public AbstractHotSource() {
        new Thread(this, "HotSource").start();
    }

    public AbstractHotSource(Listener<T>... listeners) {
        this.listeners.addAll(asList(listeners));
    }

    protected void start(){
        new Thread(this, "HotSource").start();
    }

    @Override
    public void run() {
        for (; ; ) {
            final T next = next();
            //Util.printlnThread("Emitted: " + next);
            listeners.forEach(l -> l.onReceive(next));
        }
    }

    abstract T next();

    @Override
    public void subscribe(Listener<T> listener) {
        listeners.add(listener);
    }

}