package com.supermall.shop_cart.dto;

import com.supermall.shop_cart.dto.CartDTO;

import com.supermall.shop_cart.entity.Cart;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDTO {
        @Builder.Default
        private List<CartItemDTO> items = List.of();
        private double total;

        public CartDTO convertToDTO(Cart cart) {
                List<CartItemDTO> itemDTOs = cart.getItems().stream()
                                .map(item -> CartItemDTO.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getName())
                                                .price(item.getProduct().getPrice())
                                                .quantity(item.getQuantity())
                                                .build())
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
