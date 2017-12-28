package com.getset.netfluxexample.service;

import com.getset.netfluxexample.domain.Movie;
import com.getset.netfluxexample.domain.MovieEvent;
import com.getset.netfluxexample.repositories.MovieRepositoy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepositoy movieRepositoy;

    public MovieServiceImpl(MovieRepositoy movieRepositoy) {
        this.movieRepositoy = movieRepositoy;
    }

    @Override
    public Flux<MovieEvent> events(String movieId) {
        return Flux.<MovieEvent>generate(movieEventSynchronousSink -> {
            movieEventSynchronousSink.next(new MovieEvent(movieId, new Date()));
        }).delayElements(Duration.ofSeconds(1));
    }

    @Override
    public Mono<Movie> getMovieById(String id) {
        return movieRepositoy.findById(id);
    }

    @Override
    public Flux<Movie> getAllMovies() {
        return movieRepositoy.findAll();
    }
}
