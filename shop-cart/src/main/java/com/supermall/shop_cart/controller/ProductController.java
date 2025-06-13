package com.supermall.shop_cart.controller;

import com.supermall.shop_cart.dto.ProductRequestDTO;
import com.supermall.shop_cart.dto.ProductResponseDTO;
import com.supermall.shop_cart.entity.Product;
import com.supermall.shop_cart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public Page<ProductResponseDTO> getAll(
            @RequestParam(required = false, defaultValue = "") String search,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(search, pageable).map(this::toResponseDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO create(@RequestBody ProductRequestDTO requestDTO) {
        Product product = toEntity(requestDTO);
        Product saved = productRepository.save(product);
        return toResponseDTO(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO update(@PathVariable Long id, @RequestBody ProductRequestDTO requestDTO) {
        return productRepository.findById(id).map(product -> {
            product.setName(requestDTO.getName());
            product.setDescription(requestDTO.getDescription());
            product.setPrice(requestDTO.getPrice());
            product.setStockQuantity(requestDTO.getStockQuantity());
            return toResponseDTO(productRepository.save(product));
        }).orElseThrow(() -> new RuntimeException("Product not Found!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not Found!");
        }
        productRepository.deleteById(id);
    }

    // ---Helper methods for conversion between Entity and DTO ---

    private ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }

    private Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }

}
