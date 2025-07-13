package com.event.service;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Singleton;

@Singleton
public class SeatBlockService {

    private final RedisCommands<String, String> redisCommands;


    private static final int BLOCK_TTL_SECONDS = 2 * 60;    // 2 minutes

    public SeatBlockService(StatefulRedisConnection<String, String> connection) {
        this.redisCommands = connection.sync();
    }

    // key: showId + seatNumber, value: userId
    private String getKey(String showId, String seatNumber) {
        return showId + ":" + seatNumber;
    }

    // Try blocking a seat if reserve is hit, return true if successful
    public boolean tryBlockSeat(String showId, String seatNumber, String userId) {
        String key = getKey(showId, seatNumber);
        String value = userId;
        String result = redisCommands.set(
                key, value,
                SetArgs.Builder.nx().ex(BLOCK_TTL_SECONDS)  // Set if not exists with TTL
        );
        return "OK".equals(result); // only first set wins
    }

    // Check if a seat is blocked
    public boolean isSeatBlocked(String showId, String seatNumber) {
        String key = getKey(showId, seatNumber);
        return redisCommands.exists(key) > 0;
    }


    // Release Seat Block (after payment)
    public void releaseSeatBlock(String showId, String seatNumber) {
        String key = getKey(showId, seatNumber);
        redisCommands.del(key);
    }

    // Get who blocked the seat
    public String getBlockedStringOwner(String showId, String seatNumber) {
        String key = getKey(showId, seatNumber);
        return redisCommands.get(key);
    }
}
