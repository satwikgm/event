package com.event.service;

import com.event.dto.ConfirmTicketReq;
import com.event.dto.ReserveTicketRequest;
import com.event.dto.SeatAvailabilityResponse;
import com.event.dto.Ticket;
import com.event.repository.TicketRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SeatBlockService seatBlockService;

    public Mono<String> reserveTicket(ReserveTicketRequest reserveTicketRequest) {

        return ticketRepository.findByEventAndSeat(reserveTicketRequest.getEventId(), reserveTicketRequest.getSeatNumber())
                .flatMap(ticket -> Mono.<String>error(new RuntimeException("Seat already confirmed by another user")))
                .switchIfEmpty(Mono.defer(() -> {

                    boolean tryBlockingSeat = seatBlockService.tryBlockSeat(reserveTicketRequest.getEventId(), reserveTicketRequest.getSeatNumber(), reserveTicketRequest.getUserId());

                    if (!tryBlockingSeat) { // try was not successful
                        return Mono.error(new RuntimeException("Seat already blocked by another user. Retry in sometime"));
                    }

                    return Mono.just("Seat reserved for 120 seconds");
                }));
    }

    public Mono<Ticket> confirmTicket(ConfirmTicketReq confirmTicketReq) {

        // Only allow confirm if this user holds the seat block
        String blockOwner = seatBlockService.getBlockedStringOwner(confirmTicketReq.getEventId(), confirmTicketReq.getSeatNumber());

        if (blockOwner == null || !blockOwner.equals(confirmTicketReq.getUserId())) {
            return Mono.error(new RuntimeException("Seat hold expired or is not held by you"));
        }

        return ticketRepository.findByEventAndSeat(confirmTicketReq.getEventId(), confirmTicketReq.getSeatNumber())
                .flatMap(ticket -> Mono.<Ticket>error(new RuntimeException("Seat already confirmed by another user")))
                .switchIfEmpty(Mono.defer(() -> {
                    Ticket ticket = Ticket.builder()
                            .id(UUID.randomUUID().toString())
                            .ticketId(UUID.randomUUID().toString())
                            .eventId(confirmTicketReq.getEventId())
                            .userId(confirmTicketReq.getUserId())
                            .seatNumber(confirmTicketReq.getSeatNumber())
                            .status("SEAT CONFIRMED")
                            .pricePaid(confirmTicketReq.getPrice())
                            .promotionCode(confirmTicketReq.getPromotionCode())
                            .build();
                    seatBlockService.releaseSeatBlock(confirmTicketReq.getEventId(), confirmTicketReq.getSeatNumber());
                    return ticketRepository.insertTicket(ticket);
                }));
    }

    public Mono<SeatAvailabilityResponse> checkSeatAvailability(String eventId, String seatNumber) {

        boolean blocked = seatBlockService.isSeatBlocked(eventId, seatNumber);
        if (blocked) {
            return Mono.just(SeatAvailabilityResponse.builder()
                            .eventId(eventId)
                            .available(false)
                            .seatNumber(seatNumber)
                            .message("Seat is on hold. Please try again in 2 minutes")
                    .build());
        }
        return ticketRepository.findByEventAndSeat(eventId, seatNumber)
                .map(ticket -> SeatAvailabilityResponse.builder()
                        .eventId(eventId)
                        .available(false)
                        .seatNumber(seatNumber)
                        .message("Seat Not Available. Please book another seat")
                        .build())
                .defaultIfEmpty(SeatAvailabilityResponse.builder()
                        .eventId(eventId)
                        .message("SEAT AVAILABLE")
                        .available(true)
                        .seatNumber(seatNumber)
                        .build());
    }

    public Mono<Ticket> findById(String id) {
        return ticketRepository.findTicketById(id);
    }

    public Flux<Ticket> findAll() {
        return ticketRepository.findAllTickets();
    }

}
