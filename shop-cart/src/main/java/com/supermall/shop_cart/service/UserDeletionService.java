package com.supermall.shop_cart.service;

import com.supermall.shop_cart.entity.Cart;
import com.supermall.shop_cart.entity.Order;
import com.supermall.shop_cart.entity.User;
import com.supermall.shop_cart.repository.*;
import com.supermall.shop_cart.security.TokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeletionService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public ResponseEntity<?> deleteUserById(Long id) {
        return userRepository.findById(id).map(user -> {

            // 🔐 Deletar tokens do usuário
            tokenRepository.deleteAllByUser(user);

            // 📦 Deletar pedidos e itens do pedido
            List<Order> orders = orderRepository.findByUser(user);
            for (Order order : orders) {
                orderItemRepository.deleteAll(order.getItems());
                orderRepository.delete(order);
            }
            
            // 🛒 Deletar carrinho e itens
            cartRepository.findByUser(user).ifPresent(cart -> {
                cartItemRepository.deleteAll(cart.getItems());
                cartRepository.delete(cart);
            });

            
            // 👤 Deletar o usuário
            userRepository.delete(user);





            return ResponseEntity.ok("Usuário deletado com sucesso.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
