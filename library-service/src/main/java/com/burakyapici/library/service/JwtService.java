package com.burakyapici.library.service;

import java.time.Instant;
import java.util.Map;

public interface JwtService {
    String generateAccessToken(String username);
    boolean validateToken(String token);
    Map<String, Object> extractAllClaims(String token);
    String extractSubject(String token);
    Instant extractExpiration(String token);
    <T> T extractClaim(String token, String claimKey, Class<T> type);
}
