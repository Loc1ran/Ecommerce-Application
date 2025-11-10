package com.loctran.store.controllers;

import com.loctran.store.dtos.ProductDTO;
import com.loctran.store.mappers.ProductMapper;
import com.loctran.store.repositories.ProductRepository;
import com.loctran.store.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/products")
@RestController
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductService productService;

    @GetMapping
    public List<ProductDTO> getAllProducts(
            @RequestParam(name = "categoryId", required = false) Byte categoryId
    ) {
        return productService.getAllProducts(categoryId);
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.findProductById(id);
    }

    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO);
    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public void  deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
