package com.supermall.shop_cart.service;

import com.supermall.shop_cart.entity.*;
import com.supermall.shop_cart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.supermall.shop_cart.dto.CartDTO;
import com.supermall.shop_cart.dto.CartItemDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    public Cart getCart(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Invalid User");
        }
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    public Cart addItem(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .user(user)
                    .build();
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    public void removeItem(User user, Long productId) {
        Cart cart = getOrCreateCart(user);
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> optionalItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();

            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                cart.getItems().remove(item);
                cartItemRepository.delete(item);
            }

            cartRepository.save(cart);
        } else {
            throw new RuntimeException("Item not found in Cart");
        }
    }

    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public CartDTO getCartDTO(User user) {
        Cart cart = getOrCreateCart(user);

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> {
                    Product product = item.getProduct();
                    return CartItemDTO.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .price(product.getPrice())
                            .quantity(item.getQuantity())
                            .build();
                })
                .toList();

        double total = itemDTOs.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        return CartDTO.builder()
                .items(itemDTOs)
                .total(total)
                .build();
    }

}
