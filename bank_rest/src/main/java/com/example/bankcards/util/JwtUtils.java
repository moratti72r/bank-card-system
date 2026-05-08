package com.example.bankcards.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${app.jwt-secret:myVeryStrongSecretKeyThatIsLongEnoughForHS512AndMeetsTheRFC7518Standard!}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds:86400000}")
    private long jwtExpirationMs;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            if (isTokenRevoked(authToken)) {
                return false;
            }

            JwtParser jwtParser = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build();

            Claims claims = jwtParser.parseSignedClaims(authToken).getPayload();
            Date expiration = claims.getExpiration();

            if (expiration.before(new Date())) {
                return false;
            }
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public void blacklistToken(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            Instant expiresAt = expiration.toInstant();

            if (expiresAt.isAfter(Instant.now())) {
                long ttl = expiresAt.toEpochMilli() - Instant.now().toEpochMilli();
                redisTemplate.opsForValue().set(
                        "revoked:" + token,
                        "revoked",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT Token");
        }
    }

    private boolean isTokenRevoked(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("revoked:" + token));
    }
}
