package com.getset.webfluxstockquoteservice.service;

import com.getset.webfluxstockquoteservice.domain.Quote;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class QuoteGeneratorServiceImplTest {

    QuoteGeneratorService quoteGeneratorService = new QuoteGeneratorServiceImpl();

    @Test
    public void fetchQuoteStream() throws InterruptedException {
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofSeconds(1));
        CountDownLatch countDown = new CountDownLatch(1);
        quoteFlux.take(10)
                .subscribe(System.out::println, System.err::println, () -> countDown.countDown());
        countDown.await();
    }
}