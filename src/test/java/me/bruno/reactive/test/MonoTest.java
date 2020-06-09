package me.bruno.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
/*Reative streams
* 1. Asynchronous
* 2. Non-blocking
* 3. Backpressure
* */
public class MonoTest {

    @Test
    void monoSubscriberTest() {
        String name = "Bruno Rocha";
        Mono<String> nameMono = Mono.just(name)
                .log();
        nameMono.subscribe();

        StepVerifier.create(nameMono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumerTest() {
        String name = "Bruno Rocha";
        Mono<String> nameMono = Mono.just(name)
                .log();
        nameMono.subscribe(s -> log.info("Value {}", s));

        StepVerifier.create(nameMono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumerErrorTest() {
        String name = "Bruno Rocha";
        Mono<String> nameMono = Mono.just(name)
                .map(s -> {throw new RuntimeException("Testing mono with error");});

        nameMono.subscribe(s -> log.info("Name {}", s), s -> log.error("Something bad happened"));
        nameMono.subscribe(s -> log.info("Name {}", s), Throwable::printStackTrace);

        StepVerifier.create(nameMono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void monoSubscriberConsumerCompleteTest() {
        String name = "Bruno Rocha";
        Mono<String> nameMono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        nameMono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED"));

        StepVerifier.create(nameMono)
                .expectNext(name.toUpperCase())
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumerSubscriptionTest() {
        String name = "Bruno Rocha";
        Mono<String> nameMono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        nameMono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED"),
                subscription -> subscription.request(5));

//        StepVerifier.create(nameMono)
//                .expectNext(name.toUpperCase())
//                .verifyComplete();
    }

    @Test
    void monoDoOnMethodsTest() {
        String name = "Bruno Rocha";
        Mono<String> nameMono = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(longNumber -> log.info("Request received, starting do on something..."))
                .doOnNext(s -> log.info("Value is here, Executing doOnNext {}", s))
                .doOnSuccess(s -> log.info("doOnSuccess executed"));

        nameMono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED"));
    }

    @Test
    void monoDoOnErrorTest() {

        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception error"))
                .doOnError(e -> log.error("Error message, {}", e.getMessage()))
                .doOnNext(s -> log.info("Executing doOnNext"))
                .log();

        StepVerifier.create(error)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void monoDoOnErrorResumeTest() {
        String name = "Bruno Rocha";
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception error"))
                .onErrorResume(s -> {
                    log.info("Inside on error resume");
                    return Mono.just(name);
                })
                .doOnError(e -> log.error("Error message, {}", e.getMessage()))
                .log();

        StepVerifier.create(error)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoDoOnErrorReturnTest() {
        String name = "Bruno Rocha";
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception error"))
                .onErrorReturn("EMPTY")
                .onErrorResume(s -> {
                    log.info("Inside on error resume");
                    return Mono.just(name);
                })
                .doOnError(e -> log.error("Error message, {}", e.getMessage()))
                .log();

        StepVerifier.create(error)
                .expectNext("EMPTY")
                .verifyComplete();
    }
}
