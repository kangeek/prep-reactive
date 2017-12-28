package com.getset.netfluxexample.controllers;

import com.getset.netfluxexample.domain.Movie;
import com.getset.netfluxexample.domain.MovieEvent;
import com.getset.netfluxexample.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;

@RestController
@RequestMapping("/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable String id) {
        return movieService.getMovieById(id);
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieEvent> getMovieEventsById(@PathVariable String id) {
        return movieService.events(id);
    }

    @GetMapping
    public Flux<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }
}
