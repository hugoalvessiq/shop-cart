package com.supermall.shop_cart.controller;

import com.supermall.shop_cart.dto.AuthenticationRequest;
import com.supermall.shop_cart.dto.AuthenticationResponse;
import com.supermall.shop_cart.entity.User;
import com.supermall.shop_cart.repository.UserRepository;
import com.supermall.shop_cart.security.JwtService;
import com.supermall.shop_cart.security.TokenRepository;
import com.supermall.shop_cart.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.badRequest().build();

        String token = authHeader.substring(7);
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            t.setExpired(true);
            tokenRepository.save(t);
        });

        return ResponseEntity.ok("Logout performed with revocation.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.badRequest().build();

        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUserName(refreshToken);

        User user = userRepository.findByEmail(userEmail).orElseThrow();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            return ResponseEntity.status(401).build();
        }

        String newAccessToken = jwtService.generateToken(user);
        authService.saveToken(user, newAccessToken);

        return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, refreshToken));
    }
}
