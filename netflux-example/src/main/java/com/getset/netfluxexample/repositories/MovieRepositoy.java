package com.getset.netfluxexample.repositories;

import com.getset.netfluxexample.domain.Movie;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepositoy extends ReactiveMongoRepository<Movie, String> {
}
