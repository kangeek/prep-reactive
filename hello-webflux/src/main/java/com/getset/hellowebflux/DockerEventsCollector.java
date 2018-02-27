package com.getset.hellowebflux;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.EventsResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class DockerEventsCollector implements CommandLineRunner {

    private DockerEventMongoRepository dockerEventMongoRepository;
    private ReactiveMongoTemplate mongoTemplate;

    public DockerEventsCollector(DockerEventMongoRepository dockerEventMongoRepository, ReactiveMongoTemplate mongoTemplate) {
        this.dockerEventMongoRepository = dockerEventMongoRepository;
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void run(String... args) {

        System.out.println("服务已启动，后台持续加载 docker event 数据。");

        DockerClient docker = DockerClientBuilder.getInstance("tcp://localhost:2376").build();

        Flux<DockerEvent> dockerEventFlux =
                mongoTemplate.dropCollection(DockerEvent.class).then(
                        mongoTemplate.createCollection(DockerEvent.class, CollectionOptions.empty().size(10000).maxDocuments(10).capped()))
                        .thenMany(Flux.create((FluxSink<Event> sink) -> {
                            EventsResultCallback callback = new EventsResultCallback() {
                                @Override
                                public void onNext(Event event) {
                                    sink.next(event);
                                }
                            };
                            docker.eventsCmd().exec(callback);
                        }).map(this::trans))
                .doOnNext(e -> log.info(e.toString()));
        dockerEventMongoRepository.saveAll(dockerEventFlux).subscribe();
    }

    private DockerEvent trans(Event event) {
        DockerEvent dockerEvent = new DockerEvent();
        dockerEvent.setAction(event.getAction());
        dockerEvent.setActorId(Objects.requireNonNull(event.getActor()).getId());
        dockerEvent.setFrom(event.getFrom());
        dockerEvent.setId(UUID.randomUUID().toString());
        dockerEvent.setNode(event.getNode());
        dockerEvent.setStatus(event.getStatus());
        dockerEvent.setTime(event.getTime());
        dockerEvent.setTimeNano(event.getTimeNano());
        dockerEvent.setType(event.getType());
        return dockerEvent;
    }
}
