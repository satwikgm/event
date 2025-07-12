package com.event.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.inject.Singleton;

import java.util.concurrent.TimeUnit;

@Singleton
public class SeatBlockService {

    private final Cache<String, String> seatBlocks = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    // key: showId + seatNumber, value: userId
    private String getKey(String showId, String seatNumber) {
        return showId + ":" + seatNumber;
    }

    // Try blocking a seat if reserve is hit, return true if successful
    public boolean tryBlockSeat(String showId, String seatNumber, String userId) {
        String key = getKey(showId, seatNumber);
        // putIfAbsent returns a null if seat was not reserved, i.e. key not present => Block Successful
        return seatBlocks.asMap().putIfAbsent(key, userId) == null;
    }

    // Check if a seat is blocked
    public boolean isSeatBlocked(String showId, String seatNumber) {
        String key = getKey(showId, seatNumber);
        return seatBlocks.getIfPresent(key) != null;
    }

    // Release Seat Block (after payment)
    public void releaseSeatBlock(String showId, String seatNumber) {
        String key = getKey(showId, seatNumber);
        seatBlocks.invalidate(key);
    }

    // Get who blocked the seat
    public String getBlockedStringOwner(String showId, String seatNumber) {
        String key = getKey(showId, seatNumber);
        return seatBlocks.getIfPresent(key);
    }
}
