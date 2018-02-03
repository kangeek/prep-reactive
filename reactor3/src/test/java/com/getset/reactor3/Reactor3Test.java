package com.getset.reactor3;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Reactor3Test {
    @Test
    public void testCustomedSubscriber() {
        class SampleSubscriber<T> extends BaseSubscriber<T> {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("subscribed");
                request(1);
            }

            @Override
            protected void hookOnNext(T value) {
                System.out.println(value);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                request(1);
            }
        }
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> flux = Flux.range(1, 4);
        flux.subscribe(System.out::println,
                err -> System.out.println("Error: " + err),
                () -> System.out.println("Done")
                , s -> ss.request(2)
        );
        flux.subscribe(ss);
    }

    @Test
    public void testRetry() throws InterruptedException {
        Flux.interval(Duration.ofMillis(1000))
                .map(input -> {
                    if (input < 3) return "tick " + input;
                    throw new RuntimeException("boom");
                })
                .elapsed()
                .retry(1)
                .subscribeOn(Schedulers.newElastic("myElastic"))
                .subscribe(System.out::println, System.err::println);
        TimeUnit.SECONDS.sleep(8);
    }

    @Test
    public void testRetryWhen() throws InterruptedException {
        Flux.<String>error(new IllegalAccessException())
                .doOnError(e -> System.err.println("doOnError: " + e))
                .retryWhen(companion -> companion.take(3))
                .subscribe(s -> System.out.println("subscribe with success: " + s),
                        e -> System.out.println("subscribe with error: " + e));
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void testUnicastProcessor() {
        UnicastProcessor<Integer> processor = UnicastProcessor.create();
        FluxSink<Integer> sink = processor.sink();
    }

    @Test
    public void testStepVerifier() {
        Flux<String> source = Flux.just("foo", "bar");
        Duration duration = StepVerifier.create(
                appendBoomError(source))
                .expectNext("foo")
                .expectNext("bar")
                .expectErrorMessage("boom")
                .verify();
        System.out.println(duration);

        // 虚拟时间
        duration = StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1)))
                .expectSubscription()
                .expectNoEvent(Duration.ofDays(1))
                .expectNext(0L)
                .verifyComplete();
        System.out.println(duration);
    }

    private <T> Flux<T> appendBoomError(Flux<T> source) {
        return source.concatWith(Mono.error(new IllegalArgumentException("boom")));
    }

    @Test
    public void testContext1() {
        String key = "message";
        Mono<String> r = Mono.just("hello")
                .flatMap(s -> Mono.subscriberContext().map(ctx -> s + " " + ctx.get(key)))
                .subscriberContext(ctx -> ctx.put(key, "world"))
                .flatMap(s -> Mono.subscriberContext().map(ctx -> s + " " + ctx.get(key)))
                .subscriberContext(ctx -> ctx.put(key, "Reactor"));

        StepVerifier.create(r)
                .expectNext("hello world Reactor")
                .verifyComplete();
    }

    @Test
    public void testDebugging() {
//        Hooks.onOperatorDebug();
        Mono.just("foo")
                .map(s -> null)
                .checkpoint("test1")
                .map(s -> s + ", hello!")
//                .checkpoint()
                .checkpoint("test2")
                .subscribe(System.out::println);
    }

    @Test
    public void testLogging() {
//        Flux.just(1, 2, 3, 4)
        Flux.range(1, 4)
                .log()
                .take(2)
                .subscribe(System.out::println);
    }

    @Test
    public void testParallel() {
        Flux.range(1, 10)
                .parallel(2)
                .runOn(Schedulers.parallel())
                .doOnNext(i -> System.out.println(Thread.currentThread().getName() + " -> " + i))
//                .sequential()
//                .doOnNext(System.out::println)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(Thread.currentThread().getName() + " -> " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
//                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " ---> " + i));
    }
}
