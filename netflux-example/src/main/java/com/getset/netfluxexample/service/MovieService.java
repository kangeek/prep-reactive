package com.getset.netfluxexample.service;

import com.getset.netfluxexample.domain.Movie;
import com.getset.netfluxexample.domain.MovieEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {

    Flux<MovieEvent> events(String movieId);

    Mono<Movie> getMovieById(String id);

    Flux<Movie> getAllMovies();
}
