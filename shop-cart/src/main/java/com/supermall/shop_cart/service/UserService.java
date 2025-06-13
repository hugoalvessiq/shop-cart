package com.supermall.shop_cart.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supermall.shop_cart.repository.UserRepository;
import com.supermall.shop_cart.security.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Transactional
    public ResponseEntity<?> deleteUserById(Long id) {
        return userRepository.findById(id).map(user -> {
            // Delete tokens first
            tokenRepository.deleteAllByUser(user);

            // Delete user later
            userRepository.delete(user);
            return ResponseEntity.ok().body("User deleted successfully.");
        }).orElse(ResponseEntity.notFound().build());
    }

}
