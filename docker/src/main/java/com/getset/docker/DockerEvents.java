package com.getset.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.EventsResultCallback;
import reactor.core.publisher.Flux;

import java.io.IOException;

public class DockerEvents {
    public static void main(String[] args) {
        DockerClient docker = DockerClientBuilder.getInstance("tcp://localhost:2376").build();
        Flux<Object> eventFlux = Flux.create(sink -> {
            EventsResultCallback callback = new EventsResultCallback() {
                @Override
                public void onNext(Event event) {
                    sink.next(event);
                }
            };
            try {
                docker.eventsCmd().exec(callback).awaitCompletion().close();
            } catch (InterruptedException | IOException e) {
                sink.error(e);
            }
        });

        eventFlux.subscribe(System.out::println);
    }
}
