package com.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityResponse {
    private String eventId;
    private String seatNumber;
    private boolean available;
    private String message;
}
