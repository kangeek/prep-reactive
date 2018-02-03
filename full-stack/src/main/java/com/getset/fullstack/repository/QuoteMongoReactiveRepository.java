package com.getset.fullstack.repository;

import com.getset.fullstack.domain.Quote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface QuoteMongoReactiveRepository extends ReactiveMongoRepository<Quote, String> {

    @Query("{id: {$exists: true}}")
    Flux<Quote> retrieveAllQuotesPaged(final Pageable page);
}
