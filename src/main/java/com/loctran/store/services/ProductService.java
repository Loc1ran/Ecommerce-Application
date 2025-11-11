package com.loctran.store.services;

import com.loctran.store.dtos.ProductDTO;
import com.loctran.store.entities.Category;
import com.loctran.store.entities.Product;
import com.loctran.store.exceptions.ResourceNotFoundException;
import com.loctran.store.mappers.ProductMapper;
import com.loctran.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;

    public List<ProductDTO> getAllProducts(Byte categoryId) {
        List<Product> products;

        if (categoryId != null) {
            products = productRepository.findProductByCategory_Id(categoryId);
        } else {
            products = productRepository.findAllWithCategory();
        }

        return products.stream().map(productMapper::productToProductDTO).toList();
    }

    public ProductDTO findProductById(Long id) {
        Product product = findProductEntityById(id);

        return productMapper.productToProductDTO(product);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        Category category = categoryService.findById(productDTO.getCategoryId());

        Product product = productMapper.toEntity(productDTO);
        product.setCategory(category);
        productRepository.save(product);
        productDTO.setId(product.getId());

        return productDTO;
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Category category = categoryService.findById(productDTO.getCategoryId());
        Product product = findProductEntityById(id);

        productMapper.updateProduct(productDTO, product);
        product.setCategory(category);
        productRepository.save(product);
        productDTO.setId(product.getId());

        return productDTO;
    }

    public void deleteProduct(Long id) {
        Product product = findProductEntityById(id);

        productRepository.delete(product);
    }

    private Product findProductEntityById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No product found with id " + id));
    }
}
