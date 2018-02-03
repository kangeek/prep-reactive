package com.getset.reactive.hellowebmvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return this.hello(0);
    }

    @GetMapping("/hello/{latency}")
    public String hello(@PathVariable(required = false) Integer latency) {
        try {
            TimeUnit.MILLISECONDS.sleep(latency);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hi there, How are you?";
    }
}
