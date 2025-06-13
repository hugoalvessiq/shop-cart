package com.supermall.shop_cart.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDTO {
    private Long orderId;
    private List<OrderItemDTO> items;
    private LocalDateTime orderDate;
    private double total;
    private String status;

}
