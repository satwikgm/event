package com.event.repository;

import com.event.dto.Ticket;
import com.event.exception.TicketException;
import com.event.exception.TicketInsertException;
import com.event.exception.TicketNotFoundException;
import com.event.exception.TicketUpdateException;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.bson.conversions.Bson;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.eq;

@Singleton
@RequiredArgsConstructor
public class TicketRepository {

    @Named("ticketCollection")
    private final MongoCollection<Ticket> ticketCollection;

    public Mono<Ticket> insertTicket(Ticket ticket) {
        return Mono.from(ticketCollection.insertOne(ticket))
                .then(Mono.just(ticket))
                .onErrorResume(e -> Mono.error(new TicketInsertException("Ticket Insert Failed: " + e.getMessage())));
    }

    public Mono<Ticket> findTicketById(String id) {
        return Mono.from(ticketCollection.find(eq("_id", id)).first())
                .switchIfEmpty(Mono.error(new TicketNotFoundException("Ticket not found: " + id)))
                .onErrorResume(e -> Mono.error(new TicketException("Find Failed: " + e.getMessage())));
    }

    public Mono<Ticket> updateTicket(Ticket ticket) {
        Bson filter = eq("ticketId", ticket.getTicketId());
        return Mono.from(ticketCollection.replaceOne(filter, ticket))
                .flatMap(updateResult -> updateResult.getModifiedCount() == 0 ?
                        Mono.error(new TicketUpdateException("Ticket not updated: "))
                        : Mono.just(ticket))
                .onErrorResume(e -> Mono.error(new TicketException("Update Failed: "
                        + e.getMessage())));
    }

    public Flux<Ticket> findAllTickets() {
        return Flux.from(ticketCollection.find())
                .onErrorResume(e -> Mono.error(new TicketException("Find All Failed: " + e.getMessage())));
    }

    public Mono<Ticket> findByEventAndSeat(String eventId, String seatNumber) {
        return Mono.from(
                ticketCollection.find(
                        Filters.and(
                                Filters.eq("eventId", eventId),
                                Filters.eq("seatNumber", seatNumber),
                                Filters.in("status", "CONFIRMED", "RESERVED")
                        )).first());
    }
}
