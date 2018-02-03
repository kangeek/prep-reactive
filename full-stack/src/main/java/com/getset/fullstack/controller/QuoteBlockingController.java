package com.getset.fullstack.controller;

import com.getset.fullstack.domain.Quote;
import com.getset.fullstack.repository.QuoteMongoBlockingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class QuoteBlockingController {
    private static final int DELAY_PER_ITEM_MS = 100;

    private QuoteMongoBlockingRepository quoteMongoBlockRepository;

    public QuoteBlockingController(QuoteMongoBlockingRepository quoteMongoBlockRepository) {
        this.quoteMongoBlockRepository = quoteMongoBlockRepository;
    }

    @GetMapping("/quote-list")
    public List<Quote> getQuoteList() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(DELAY_PER_ITEM_MS * quoteMongoBlockRepository.count());
        return this.quoteMongoBlockRepository.findAll();
    }

    @GetMapping("/quote-list-paged")
    public List<Quote> getPagedQuoteList(@RequestParam(name = "page") final int page,
                                         @RequestParam(name = "size") final int size) {
        return this.quoteMongoBlockRepository.retrieveAllQuotesPaged(PageRequest.of(page, size));
    }
}
