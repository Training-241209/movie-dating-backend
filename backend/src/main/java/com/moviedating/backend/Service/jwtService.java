package com.moviedating.backend.Service;

import com.moviedating.backend.Entity.Account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

@Service
public class jwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    private final Set<String> invalidTokens = new HashSet<>();

    public void invalidateToken(String token) {
        invalidTokens.add(token);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Account account) {
        return Jwts.builder()
                .claim("username", account.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutes
                .signWith(getSigningKey())
                .compact();
    }

    public Account decodeToken(String token) {
        @SuppressWarnings("deprecation")
        var claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getBody();

        Account account = new Account();
        account.setUsername(claims.get("username", String.class));
        return account;
    }

}