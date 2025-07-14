package com.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private String eventId;
    private String userId;
    private String seatNumber;
    private String ticketId;
    private String type;    // CONFIRMED, CANCELLED
    private String email;
    private LocalDateTime timestamp;
}
