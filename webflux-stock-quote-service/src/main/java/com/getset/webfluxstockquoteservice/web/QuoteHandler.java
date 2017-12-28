package com.getset.webfluxstockquoteservice.web;

import com.getset.webfluxstockquoteservice.domain.Quote;
import com.getset.webfluxstockquoteservice.service.QuoteGeneratorService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class QuoteHandler {
    private final QuoteGeneratorService quoteGeneratorService;

    public QuoteHandler(QuoteGeneratorService quoteGeneratorService) {
        this.quoteGeneratorService = quoteGeneratorService;
    }

    public Mono<ServerResponse> fetchQuotes(ServerRequest request) {
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        return ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(this.quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(200))
                    .take(size), Quote.class);
    }

    public Mono<ServerResponse> streamQuotes(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(this.quoteGeneratorService.fetchQuoteStream(Duration.ofSeconds(1)), Quote.class);
    }
}
