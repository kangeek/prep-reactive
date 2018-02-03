package com.getset.fullstack.configuration;

import com.getset.fullstack.domain.Quote;
import com.getset.fullstack.repository.QuoteMongoReactiveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

@Component
public class QuijoteDataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(QuijoteDataLoader.class);

    private QuoteMongoReactiveRepository quoteMongoReactiveRepository;

    public QuijoteDataLoader(QuoteMongoReactiveRepository quoteMongoReactiveRepository) {
        this.quoteMongoReactiveRepository = quoteMongoReactiveRepository;
    }

    @Override
    public void run(String... args) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("page2000.txt")));
        Flux.fromStream(
                bufferedReader.lines().filter(line -> !line.trim().isEmpty())
                .map(line -> quoteMongoReactiveRepository.save(new Quote(UUID.randomUUID().toString(), "El Quijote", line)))
        ).subscribe(m -> logger.info("new quote loaded: " + m.block()));

        logger.info("repository new contains " + quoteMongoReactiveRepository.count().block() + " quotes.");
    }
}
