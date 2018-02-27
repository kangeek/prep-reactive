package com.getset.hellowebflux;

import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface DockerEventMongoRepository extends ReactiveCrudRepository<DockerEvent, String> {
    @Tailable
    Flux<DockerEvent> findByStatus(String status);

    @Tailable
    Flux<DockerEvent> findByTypeAndFrom(String type, String from);

    @Tailable
    Flux<DockerEvent> findBy();
}
