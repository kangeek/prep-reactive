package com.getset.fullstack.controller;

import com.getset.fullstack.domain.Quote;
import com.getset.fullstack.repository.QuoteMongoReactiveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class QuoteReactiveController {

    private static final Logger logger = LoggerFactory.getLogger(QuoteReactiveController.class);
    private static final int DELAY_PER_ITEM_MS = 100;

    private QuoteMongoReactiveRepository quoteMongoReactiveRepository;

    public QuoteReactiveController(QuoteMongoReactiveRepository quoteMongoReactiveRepository) {
        this.quoteMongoReactiveRepository = quoteMongoReactiveRepository;
    }

    @GetMapping(value = "/quote-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Quote> getQuoteFlux() {
        return this.quoteMongoReactiveRepository.findAll().doOnNext(e -> logger.info("get a quote: " + e)).delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

    @GetMapping("/quote-flux-paged")
    public Flux<Quote> getPagedQuoteFlux(@RequestParam(name = "page") final int page,
                                         @RequestParam(name = "size") final int size) {
        return this.quoteMongoReactiveRepository.retrieveAllQuotesPaged(PageRequest.of(page, size))
                .delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }
}
