package com.getset.fullstack.repository;

import com.getset.fullstack.domain.Quote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuoteMongoBlockingRepository extends MongoRepository<Quote, String> {
    @Query("{id: {$exists: true}}")
    List<Quote> retrieveAllQuotesPaged(final Pageable page);
}
