package com.getset.scrawls.async;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RandomDataServiceTest {
    private final int TIMES = 100;
    private final CountDownLatch countDownLatch = new CountDownLatch(TIMES);
    private RandomDataService randomDataService;
    private long start;

    @Before
    public void before() {
        randomDataService = new RandomDataService();
        start = System.currentTimeMillis();
    }

    @Test
    public void runTwiceSuccessively() {
        System.out.print(randomDataService.getRandomInt());

        System.out.print(randomDataService.getRandomInt());
    }

    @Test
    public void runTwiceConcurrently() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new Runnable() {
            public void run() {
                System.out.print(randomDataService.getRandomInt());
                countDownLatch.countDown();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                System.out.print(randomDataService.getRandomInt());
                countDownLatch.countDown();
            }
        }).start();
        countDownLatch.await();
    }

    @Test
    public void loopSuccessively() {
        for (int i = 0; i < TIMES; i++) {
            System.out.print(randomDataService.getRandomInt());
        }
    }

    @Test
    public void loopConcurrently() throws InterruptedException {
        for (int i = 0; i < TIMES; i++) {
            new Thread(new Runnable() {
                public void run() {
                    System.out.print(randomDataService.getRandomInt());
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }

    @Test
    public void executor() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < TIMES; i++) {
            executorService.submit(new Runnable() {
                public void run() {
                    System.out.print(randomDataService.getRandomInt() + " ");
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    @Test
    public void callback() throws InterruptedException {
        for (int i = 0; i < TIMES; i++) {
            randomDataService.getRandomInt(new Callback() {
                public void onFinish(String result) {
                    System.out.print(result + " ");
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }

    @Test
    public void useFuture() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < TIMES; i++) {
            CompletableFuture.supplyAsync(new Supplier<String>() {
                @Override
                public String get() {
                    return randomDataService.getRandomInt();
                }
//            }).thenAccept(new Consumer<Integer>() {
            }, executor).thenAccept(new Consumer<String>() {
                @Override
                public void accept(String string) {
                    System.out.print(string + " ");
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }


    @After
    public void after() {
        System.out.println("\nThis process took " + (System.currentTimeMillis() - start) + " milliseconds.");
    }

}