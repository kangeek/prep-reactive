package com.getset.netfluxexample.bootstrap;

import com.getset.netfluxexample.domain.Movie;
import com.getset.netfluxexample.repositories.MovieRepositoy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
public class BootstrapCLR implements CommandLineRunner {
    private final MovieRepositoy movieRepositoy;

    public BootstrapCLR(MovieRepositoy movieRepositoy) {
        this.movieRepositoy = movieRepositoy;
    }

    @Override
    public void run(String... args) throws Exception {
        movieRepositoy.deleteAll().block();
        Flux.just("movie1", "movie2", "movie3", "movie4", "movie5", "movie6")
                .map(title -> new Movie(UUID.randomUUID().toString(), title))
                .flatMap(movieRepositoy::save)
                .subscribe(null, null, () -> {
                    movieRepositoy.findAll().subscribe(System.out::println);
                });
    }
}
