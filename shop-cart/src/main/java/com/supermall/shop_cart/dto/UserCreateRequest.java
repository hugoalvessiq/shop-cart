package com.supermall.shop_cart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "Name is required") String name,

        @Email(message = "Invalid Email") @Email String email,

        @Size(min = 6, message = "Password must be at least 6 characters long") String password,

        String role // optional, can be "USER" or "ADMIN"
) {
}
