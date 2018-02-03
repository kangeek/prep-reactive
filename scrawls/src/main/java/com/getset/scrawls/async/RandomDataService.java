package com.getset.scrawls.async;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomDataService {

    private static AtomicInteger counter = new AtomicInteger();
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public String getRandomInt() {
        int num = counter.incrementAndGet();
        int result = ThreadLocalRandom.current().nextInt(100);
        try {
            TimeUnit.MILLISECONDS.sleep(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return " [" + num + "]" +  result;
    }


    public void getRandomInt(final Callback callback) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                callback.onFinish(getRandomInt());
            }
        });
    }
}
