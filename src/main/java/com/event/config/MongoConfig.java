package com.event.config;

import com.event.dto.Ticket;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Factory
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoClient mongoClient;

    @Singleton
    public MongoDatabase mongoDatabase() {
        return mongoClient.getDatabase("ticket-db");
    }

    @Singleton
    @Named("ticketCollection")
    public MongoCollection<Ticket> getTicketCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection("tickets", Ticket.class);
    }
}
