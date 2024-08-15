package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtTokenService {
    private final JwtEncoder jwtEncoder;
    @Value("${jwt.expires-in}")
    private long expiresIn;

    public JwtTokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateJwtToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claim = JwtClaimsSet.builder()
                .issuer("google-auth-app")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(this.expiresIn))
                .build();

        return  jwtEncoder.encode(JwtEncoderParameters.from(claim)).getTokenValue();
    }
}
