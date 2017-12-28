package com.getset.webfluxstockquoteservice.service;

import com.getset.webfluxstockquoteservice.domain.Quote;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

@Service
public class QuoteGeneratorServiceImpl implements QuoteGeneratorService {
    private final MathContext MATH_CONTEXT = new MathContext(2);
    private final Random random = new Random();
    private final List<Quote> prices = new ArrayList<>();

    public QuoteGeneratorServiceImpl() {
        prices.add(new Quote("AAA", 100.0));
        prices.add(new Quote("BBB", 110.0));
        prices.add(new Quote("CCC", 120.0));
        prices.add(new Quote("DDD", 130.0));
        prices.add(new Quote("EEE", 140.0));
        prices.add(new Quote("FFF", 150.0));
        prices.add(new Quote("GGG", 160.0));
        prices.add(new Quote("HHH", 170.0));
    }

    @Override
    public Flux<Quote> fetchQuoteStream(Duration period) {
        return Flux.generate(() -> 0,
                (BiFunction<Integer, SynchronousSink<Quote>, Integer>)(index, sink) -> {
                    Quote updatedQuote = updateQuote(this.prices.get(index));
                    sink.next(updatedQuote);
                    return ++index % this.prices.size();
                })
                .zipWith(Flux.interval(period))
                .map(t -> t.getT1())
                .map(quote -> {
                    quote.setInstant(Instant.now());
                    return quote;
                })
                .log("com.getset.webfluxstockquoteservice.QueteGeneratorService");
    }

    private Quote updateQuote(Quote quote) {
        BigDecimal newPrice = quote.getPrice().multiply(new BigDecimal(1 + 0.05 * random.nextDouble()), this.MATH_CONTEXT);
        return new Quote(quote.getTicker(), newPrice);
    }
}
