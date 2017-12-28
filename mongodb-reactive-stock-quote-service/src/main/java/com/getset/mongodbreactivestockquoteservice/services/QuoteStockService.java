package com.getset.mongodbreactivestockquoteservice.services;

import com.getset.mongodbreactivestockquoteservice.clients.StockQuoteClient;
import com.getset.mongodbreactivestockquoteservice.domains.Quote;
import com.getset.mongodbreactivestockquoteservice.reposities.QuoteRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class QuoteStockService implements ApplicationListener<ContextRefreshedEvent> {
    private final StockQuoteClient stockQuoteClient;
    private final QuoteRepository quoteStockService;

    public QuoteStockService(StockQuoteClient stockQuoteClient, QuoteRepository quoteStockService) {
        this.stockQuoteClient = stockQuoteClient;
        this.quoteStockService = quoteStockService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        stockQuoteClient.getQuoteStream()
                .log("mogodb-reactive-stock-quote-service")
                .subscribe(quote -> {
                    Mono<Quote> savedQuote = quoteStockService.save(quote);
                    System.out.println("I saved a quote! id(before): " + quote.getId());
                    System.out.println("I saved a quote! id(after): " + savedQuote.block().getId());
                });
    }
}
