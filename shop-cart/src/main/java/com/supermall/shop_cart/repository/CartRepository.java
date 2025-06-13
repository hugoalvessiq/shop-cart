package com.supermall.shop_cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supermall.shop_cart.entity.Cart;
import com.supermall.shop_cart.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
