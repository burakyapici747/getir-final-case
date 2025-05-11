package com.burakyapici.library.service.impl;

import com.burakyapici.library.service.JwtService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {
    private static final Duration ACCESS_TTL = Duration.ofMinutes(5);
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtServiceImpl(JwtEncoder encoder, JwtDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public String generateAccessToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("library-service")
            .issuedAt(now)
            .expiresAt(now.plus(ACCESS_TTL))
            .subject(username)
            .claim("type", "access")
            .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            decoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> extractAllClaims(String token) {
        Jwt jwt = decoder.decode(token);
        return jwt.getClaims();
    }

    @Override
    public String extractSubject(String token) {
        return decoder.decode(token).getSubject();
    }

    @Override
    public Instant extractExpiration(String token) {
        return decoder.decode(token).getExpiresAt();
    }

    @Override
    public <T> T extractClaim(String token, String claimKey, Class<T> type) {
        Object val = decoder.decode(token).getClaim(claimKey);
        return type.cast(val);
    }
}
