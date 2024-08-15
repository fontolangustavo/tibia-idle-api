package com.fontolan.tibiaidle.controllers;

import com.fontolan.tibiaidle.controllers.mappers.AuthMapper;
import com.fontolan.tibiaidle.controllers.request.TokenRequest;
import com.fontolan.tibiaidle.controllers.response.AuthResponse;
import com.fontolan.tibiaidle.entities.User;
import com.fontolan.tibiaidle.repositories.UserRepository;
import com.fontolan.tibiaidle.services.GoogleTokenService;
import com.fontolan.tibiaidle.services.JwtTokenService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final GoogleTokenService googleTokenService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    public AuthController(GoogleTokenService googleTokenService, JwtTokenService jwtTokenService, UserRepository userRepository, AuthMapper authMapper) {
        this.googleTokenService = googleTokenService;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.authMapper = authMapper;
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody TokenRequest tokenRequest) throws Exception {
        GoogleIdToken idToken = googleTokenService.verifyToken(tokenRequest.getToken());
        GoogleIdToken.Payload payload = idToken.getPayload();

        String googleId = payload.getSubject();
        String email = payload.getEmail();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = User.builder()
                .googleId(googleId)
                .email(email)
                .build();

            userRepository.save(user);
        }

        String token = jwtTokenService.generateJwtToken(user);

        var response = authMapper.toAuthResponse(token, user);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
