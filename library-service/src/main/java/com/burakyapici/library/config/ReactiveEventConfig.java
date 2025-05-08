package com.burakyapici.library.config;

import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class ReactiveEventConfig {
    private final Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink =
        Sinks.many().multicast().onBackpressureBuffer();

    @Bean
    public Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink() {
        return bookAvailabilitySink;
    }

    @Bean
    public Flux<BookAvailabilityUpdateEvent> bookAvailabilityFlux() {
        return bookAvailabilitySink.asFlux();
    }
}
