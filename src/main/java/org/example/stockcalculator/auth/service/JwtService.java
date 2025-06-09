package org.example.stockcalculator.auth.service;

import java.security.Key;
import java.util.Date;

import org.example.stockcalculator.entity.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private static final long EXPIRATION_MS = 86400000; // 24 hours

    private final Key key;

    public JwtService(@Value("${auth.jwt.secret}") String jwtSecret) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateTokenForAccount(UserAccount account) {
        return Jwts.builder()
                .setSubject(account.getId().toString())
                .claim("email", account.getEmail())
                .claim("givenName", account.getGivenName())
                .claim("familyName", account.getFamilyName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
