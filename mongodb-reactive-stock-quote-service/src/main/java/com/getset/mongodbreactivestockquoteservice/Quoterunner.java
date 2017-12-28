package com.getset.mongodbreactivestockquoteservice;

import com.getset.mongodbreactivestockquoteservice.clients.StockQuoteClient;
import com.getset.mongodbreactivestockquoteservice.domains.Quote;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

//@Component
public class Quoterunner implements CommandLineRunner {
    private final StockQuoteClient stockQuoteClient;

    public Quoterunner(StockQuoteClient stockQuoteClient) {
        this.stockQuoteClient = stockQuoteClient;
    }

    @Override
    public void run(String... args) throws Exception {
        Flux<Quote> quoteFlux = stockQuoteClient.getQuoteStream();
        quoteFlux.subscribe(System.out::println);
    }
}
