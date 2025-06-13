package com.supermall.shop_cart.service;

import com.supermall.shop_cart.dto.AuthenticationRequest;
import com.supermall.shop_cart.dto.AuthenticationResponse;
import com.supermall.shop_cart.entity.User;
import com.supermall.shop_cart.repository.UserRepository;
import com.supermall.shop_cart.security.JwtService;
import com.supermall.shop_cart.security.Token;
import com.supermall.shop_cart.security.TokenRepository;
import com.supermall.shop_cart.security.TokenType;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UserRepository userRepository;
        private final TokenRepository tokenRepository;
        private final JwtService jwtService;
        private final AuthenticationManager authManager;

        public AuthenticationResponse authenticate(AuthenticationRequest request) {

                authManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String accessToken = jwtService.generateToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);

                revokedAllTokens(user);
                saveToken(user, accessToken);

                return new AuthenticationResponse(accessToken, refreshToken);

        }

        public void saveToken(User user, String jwtToken) {
                Token token = Token.builder()
                                .user(user)
                                .token(jwtToken)
                                .tokenType(TokenType.BEARER)
                                .expired(false)
                                .revoked(false)
                                .build();
                tokenRepository.save(token);
        }

        private void revokedAllTokens(User user) {
                var validTokens = tokenRepository.findAllByUser(user);
                validTokens.forEach(t -> {
                        t.setExpired(true);
                        t.setRevoked(true);
                });
                tokenRepository.saveAll(validTokens);
        }

}
