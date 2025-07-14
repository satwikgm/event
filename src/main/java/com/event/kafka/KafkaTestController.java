package com.event.kafka;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/test")
public class KafkaTestController {

    @Inject
    BookingEventProducer eventProducer;

    @Get("/kafka")
    public String sendTestKafka() {
        String message = "Hello: " + System.currentTimeMillis();
        eventProducer.send(message);
        return "Sent: " + message;
    }
}
