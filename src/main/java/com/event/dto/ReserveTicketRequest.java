package com.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveTicketRequest {
    private String eventId;
    private String userId;
    private String seatNumber;
    private BigDecimal price;
    private String promotionCode;
}
