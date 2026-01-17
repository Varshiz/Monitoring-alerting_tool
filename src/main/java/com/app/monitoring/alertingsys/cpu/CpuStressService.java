package com.app.monitoring.alertingsys.cpu;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CpuStressService {

    private final List<Thread> workers = new ArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);

    public void start(int threads) {
        if (running.get()) return;
        running.set(true);
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                while (running.get()) {
                    Math.sqrt(System.nanoTime());
                }
            });
            t.start();
            workers.add(t);
        }
    }

    public void stop() {
        running.set(false);
        workers.clear();
    }
}
