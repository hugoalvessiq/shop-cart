package com.supermall.shop_cart.controller;

import com.supermall.shop_cart.dto.*;
import com.supermall.shop_cart.entity.*;
import com.supermall.shop_cart.repository.*;
import com.supermall.shop_cart.security.JwtService;
import com.supermall.shop_cart.security.TokenRepository;
import com.supermall.shop_cart.service.AuthenticationService;
import com.supermall.shop_cart.service.UserDeletionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationService authService;
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserDeletionService userDeletionService;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(409).body("Email is already in use!");
        }

        User.Role role = User.Role.USER;
        if (request.role() != null) {
            try {
                role = User.Role.valueOf(request.role().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid Role: " + request.role());
            }
        }

        if (role == User.Role.ADMIN) {
            User currentUser = getCurrentUser();
            if (currentUser.getRole() != User.Role.ADMIN) {
                return ResponseEntity.status(403).body("Only ADMIN can create ADMIN users.");
            }
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        User saved = userRepository.save(user);

        UserResponseDto response = new UserResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole().name());

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getall")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(u -> new UserResponseDto(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRole().name()))
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserId(@PathVariable Long id) {
        User currentUser = getCurrentUser();

        if (!currentUser.getRole().equals(User.Role.ADMIN) && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).build();
        }

        return userRepository.findById(id)
                .map(u -> new UserResponseDto(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRole().name()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        User currentUser = getCurrentUser();

        if (!currentUser.getRole().equals(User.Role.ADMIN) && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        return userRepository.findById(id).map(user -> {
            if (request.name() != null)
                user.setName(request.name());
            if (request.email() != null)
                user.setEmail(request.email());
            if (request.password() != null && !request.password().isEmpty()) {
                user.setPassword(passwordEncoder.encode(request.password()));
            }

            userRepository.save(user);

            tokenRepository.findAllByUser(user).forEach(t -> {
                t.setExpired(true);
                t.setRevoked(true);
            });
            tokenRepository.saveAll(tokenRepository.findAllByUser(user));

            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);
            authService.saveToken(user, newAccessToken);

            UserResponseDto userResponse = new UserResponseDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name());

            AuthenticationResponse tokenResponse = new AuthenticationResponse(
                    newAccessToken,
                    newRefreshToken);

            return ResponseEntity.ok(new UserUpdateResponse(userResponse, tokenResponse));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isAdmin = currentUser.getRole().equals(User.Role.ADMIN);

        if (isAdmin) {
            return userDeletionService.deleteUserById(id);
        }

        if (!id.equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only delete your own account.");
        }

        return userDeletionService.deleteUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/test")
    public ResponseEntity<String> testAdminOnly() {
        return ResponseEntity.ok("You are an ADMIN");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile() {
        User user = getCurrentUser();
        return ResponseEntity.ok(new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()));
    }
}
