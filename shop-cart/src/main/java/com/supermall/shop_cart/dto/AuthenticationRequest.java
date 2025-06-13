package com.supermall.shop_cart.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}