package com.loctran.store.services;

import com.loctran.store.dtos.ProductDTO;
import com.loctran.store.entities.Product;
import com.loctran.store.mappers.ProductMapper;
import com.loctran.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDTO> getAllProducts(Byte categoryId) {
        List<Product> products = new ArrayList<>();
        if (categoryId != null) {
            products = productRepository.findProductByCategory_Id(categoryId);
        } else {
            products = productRepository.findAllWithCategory();
        }

        return products.stream().map(productMapper::productToProductDTO).toList();

    }

    public ResponseEntity<ProductDTO> findProductById(Long id) {
        var product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productMapper.productToProductDTO(product));
    }
}
