package com.event.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface BookingEventProducer {
    @Topic("booking-events")
//    void send(BookingEvent event);
    void send(String message);
    // publish an event after successful booking.
}
