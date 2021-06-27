package com.tehilafi.ama;

import java.util.concurrent.atomic.AtomicBoolean;

public class Functionality {

    private final AtomicBoolean done = new AtomicBoolean();

    public void run(Runnable task) {
        if (done.get()) return;
        if (done.compareAndSet(false, true)) {
            task.run();
        }
    }
}

