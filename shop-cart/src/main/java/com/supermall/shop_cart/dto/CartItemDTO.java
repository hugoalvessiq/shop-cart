package com.supermall.shop_cart.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
}
