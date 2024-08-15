package com.fontolan.tibiaidle.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenService {
    @Value("${google.client.id}")
    private String clientId;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public GoogleIdToken verifyToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY
            )
            .setAudience(Collections.singletonList(clientId))
            .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken == null) {
            throw new Exception("Token ID inválido ou não pôde ser verificado.");
        }

        return idToken;
    }
}
