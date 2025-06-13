package com.supermall.shop_cart.repository;

import java.util.Optional;

import com.supermall.shop_cart.entity.CartItem;
import com.supermall.shop_cart.entity.Product;
import com.supermall.shop_cart.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserAndProduct(User user, Product product);


}