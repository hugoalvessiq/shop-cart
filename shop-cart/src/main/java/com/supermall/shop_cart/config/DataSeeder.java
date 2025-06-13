package com.supermall.shop_cart.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.supermall.shop_cart.entity.Product;
import com.supermall.shop_cart.entity.User;
import com.supermall.shop_cart.repository.ProductRepository;
import com.supermall.shop_cart.repository.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initiDatabase(UserRepository userRepo, ProductRepository productRepo, PasswordEncoder encoder) {
        return args -> {
            // Creates the default admin
            if (userRepo.count() == 0) {
                User user = User.builder()
                        .name("Test Customer")
                        .email("test@email.com")
                        .password(encoder.encode("123456")) 
                        .role(User.Role.ADMIN)
                        .build();
                userRepo.save(user);
            }

            if (productRepo.count() == 0) {
                Product p1 = new Product(null, "T-shirt", "Cotton T-shirt", 49.09, 20);
                Product p2 = new Product(null, "Jeans", "Dark blue", 99.80, 10);

                productRepo.save(p1);
                productRepo.save(p2);
            }
        };
    }
}

