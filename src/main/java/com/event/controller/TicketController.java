package com.event.controller;

import com.event.dto.*;
import com.event.service.TicketService;
import io.micronaut.http.annotation.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Get("/event/{eventId}/seat/{seatNumber}/availability")
    public Mono<SeatAvailabilityResponse> checkSeatAvailability(String eventId, String seatNumber) {
        return ticketService.checkSeatAvailability(eventId, seatNumber);
    }

    @Post("/reserve")
    public Mono<TicketReserveResponse> reserveTicket(@Body ReserveTicketRequest reserveTicketRequest) {
        return ticketService.reserveTicket(reserveTicketRequest)
                .map(ticket -> TicketReserveResponse.builder()
                        .message("TICKET RESERVED: " + "Seat Hold Valid for 120 seconds")
                        .status("RESERVED")
                        .build());
    }

    @Post("/confirm")
    public Mono<TicketConfirmResponse> confirmTicket(@Body ConfirmTicketReq confirmTicketReq) {
        return ticketService.confirmTicket(confirmTicketReq)
                .map(ticket -> TicketConfirmResponse.builder()
                        .ticketId(ticket.getTicketId())
                        .message("TICKET CONFIRMED")
                        .status("CONFIRMED")
                        .build());
    }

    @Get("/{id}")
    public Mono<Ticket> getById(@PathVariable String id) {
        return ticketService.findById(id);
    }

    @Get("/")
    public Flux<Ticket> getAll() {
        return ticketService.findAll();
    }
}
