package com.getset.hellowebflux;

import com.github.dockerjava.api.model.Event;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMongoRepository extends ReactiveMongoRepository<Event, String> {
}
