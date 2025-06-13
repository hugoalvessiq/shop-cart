package com.supermall.shop_cart.controller;

import com.supermall.shop_cart.dto.CartDTO;
import com.supermall.shop_cart.entity.User;

import com.supermall.shop_cart.repository.UserRepository;
import com.supermall.shop_cart.service.CartService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        User user = getCurrentUser();
        CartDTO cartDTO = cartService.getCartDTO(user);
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<CartDTO> addItem(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        User user = getCurrentUser();
        cartService.addItem(user, productId, quantity);
        CartDTO cartDTO = cartService.getCartDTO(user);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable Long productId) {
        try {
            cartService.removeItem(getCurrentUser(), productId);
            return ResponseEntity.ok("Item removed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        cartService.clearCart(getCurrentUser());
        return ResponseEntity.ok("Cart successfully cleaned.");
    }

    @GetMapping("/dto")
    public ResponseEntity<CartDTO> getMyCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return ResponseEntity.ok(cartService.getCartDTO(user));
    }

}
