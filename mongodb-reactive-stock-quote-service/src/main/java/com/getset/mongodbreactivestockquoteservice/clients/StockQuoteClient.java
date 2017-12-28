package com.getset.mongodbreactivestockquoteservice.clients;

import com.getset.mongodbreactivestockquoteservice.domains.Quote;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "getset")
public class StockQuoteClient {
    private String host;
    private String port;
    private String path;

    public Flux<Quote> getQuoteStream() {
        String url = "http://" + host + ":" + port;
        log.debug("Uri is set: " + url);
        return WebClient.builder()
                .baseUrl(url)
                .build()
                .get()
                .uri(path)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(Quote.class);
    }
}
