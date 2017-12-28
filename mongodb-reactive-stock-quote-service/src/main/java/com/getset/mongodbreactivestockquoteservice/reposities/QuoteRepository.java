package com.getset.mongodbreactivestockquoteservice.reposities;

import com.getset.mongodbreactivestockquoteservice.domains.Quote;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface QuoteRepository extends ReactiveMongoRepository<Quote, String> {
}
