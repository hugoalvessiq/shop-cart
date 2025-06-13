package com.supermall.shop_cart.service;

import com.supermall.shop_cart.controller.UserController;
import com.supermall.shop_cart.dto.OrderDTO;
import com.supermall.shop_cart.dto.OrderItemDTO;
import com.supermall.shop_cart.entity.*;
import com.supermall.shop_cart.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class OrderService {

        private final OrderRepository orderRepository;
        private final CartRepository cartRepository;
        private final ProductRepository productRepository;
        private final CartItemRepository cartItemRepository;
        private final OrderItemRepository orderItemRepository;
        private final UserController userController;

        public Order placeOrder(User user) {
                Cart cart = cartRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));

                if (cart.getItems().isEmpty()) {
                        throw new RuntimeException("Empty Cart");
                }

                List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
                        return OrderItem.builder()
                                        .product(cartItem.getProduct())
                                        .quantity(cartItem.getQuantity())
                                        .price(cartItem.getProduct().getPrice())
                                        .build();
                }).collect(Collectors.toList());

                double total = orderItems.stream()
                                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                                .sum();

                Order order = Order.builder()
                                .orderDate(LocalDateTime.now())
                                .status(Order.Status.PENDING)
                                .total(total)
                                .user(user)
                                .items(orderItems)
                                .build();

                order = orderRepository.save(order);

                for (OrderItem item : orderItems) {
                        item.setOrder(order);
                        orderItemRepository.save(item);
                }

                // Clean cart
                cartItemRepository.deleteAll(cart.getItems());

                return order;

        }

        public List<Order> getOrders(User user) {
                return orderRepository.findByUser(user);
        }

        public List<OrderDTO> getOrderDTOs(User user) {
                List<Order> orders = orderRepository.findByUser(user);

                return orders.stream().map(order -> OrderDTO.builder()
                                .orderId(order.getId())
                                .orderDate(order.getOrderDate())
                                .total(order.getTotal())
                                .status(order.getStatus().name())
                                .items(order.getItems().stream().map(item -> OrderItemDTO.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getName())
                                                .price(item.getPrice())
                                                .quantity(item.getQuantity())
                                                .build()).toList())
                                .build()).toList();
        }

        public OrderDTO checkout(User user) {
                Cart cart = cartRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Cart not Found!"));

                if (cart.getItems().isEmpty()) {
                        throw new RuntimeException("Empty Cart");
                }

                // Create order
                Order order = Order.builder()
                                .user(user)
                                .orderDate(LocalDateTime.now())
                                .items(new ArrayList<>())
                                .status(Order.Status.PENDING)
                                .build();

                order = orderRepository.save(order);
                double total = 0.0;

                for (CartItem cartItem : cart.getItems()) {
                        Product product = cartItem.getProduct();
                        int quantity = cartItem.getQuantity();
                        double price = product.getPrice();

                        if (product.getStockQuantity() < quantity) {
                                throw new RuntimeException("Insufficient stock for the product: " + product.getName());
                        }

                        // reduce stock
                        product.setStockQuantity(product.getStockQuantity() - quantity);
                        productRepository.save(product);

                        OrderItem orderItem = OrderItem.builder()
                                        .order(order)
                                        .product(product)
                                        .quantity(quantity)
                                        .price(price)
                                        .build();

                        order.getItems().add(orderItem);
                        orderItemRepository.save(orderItem);

                        total += price * quantity;
                }

                order.setTotal(total);
                order = orderRepository.save(order);

                // Clean cart
                cartItemRepository.deleteAll(cart.getItems());
                cart.getItems().clear();
                cartRepository.save(cart);

                // Return DTO
                List<OrderItemDTO> itemDTOs = order.getItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getName())
                                                .quantity(item.getQuantity())
                                                .price(item.getPrice())
                                                .build())
                                .collect(Collectors.toList());

                return OrderDTO.builder()
                                .orderId(order.getId())
                                .items(itemDTOs)
                                .total(order.getTotal())
                                .orderDate(order.getOrderDate())
                                .build();
        }

        @Transactional
        public OrderDTO createOrder() {
                User user = userController.getCurrentUser();
                Cart cart = cartRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));

                if (cart.getItems().isEmpty()) {
                        throw new RuntimeException("empty Cart");
                }

                Order order = new Order();
                order.setUser(user);
                order.setOrderDate(LocalDateTime.now());
                order.setStatus(Order.Status.PENDING);
                order = orderRepository.save(order);

                double total = 0.0;

                for (CartItem item : cart.getItems()) {
                        Product product = item.getProduct();

                        if (product.getStockQuantity() < item.getQuantity()) {
                                throw new RuntimeException("Insufficient stock for the product: " + product.getName());
                        }

                        product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                        productRepository.save(product);

                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrder(order);
                        orderItem.setProduct(product);
                        orderItem.setQuantity(item.getQuantity());
                        orderItem.setPrice(product.getPrice());
                        orderItemRepository.save(orderItem);

                        total += item.getQuantity() * product.getPrice();
                }

                order.setTotal(total);
                order = orderRepository.save(order);

                cart.getItems().clear();
                cartRepository.save(cart);

                List<OrderItemDTO> itemDTOs = order.getItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getName())
                                                .quantity(item.getQuantity())
                                                .price(item.getPrice())
                                                .build())
                                .toList();

                return OrderDTO.builder()
                                .orderId(order.getId())
                                .items(itemDTOs)
                                .total(order.getTotal())
                                .orderDate(order.getOrderDate())
                                .status(order.getStatus().name())
                                .build();
        }

}
