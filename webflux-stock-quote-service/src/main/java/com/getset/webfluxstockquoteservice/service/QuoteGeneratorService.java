package com.getset.webfluxstockquoteservice.service;

import com.getset.webfluxstockquoteservice.domain.Quote;
import reactor.core.publisher.Flux;

import java.time.Duration;

public interface QuoteGeneratorService {
    Flux<Quote> fetchQuoteStream(Duration period);
}
