package com.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @BsonId
    private String id;
    private String ticketId;
    private String eventId;
    private String userId;
    private String seatNumber;
    private String status; // RESERVED, CONFIRMED, CANCELLED
    private BigDecimal pricePaid;
    private String promotionCode;
    private BigDecimal discountApplied;
    private String qrCodeData;
    private LocalDateTime bookedAt;
}
