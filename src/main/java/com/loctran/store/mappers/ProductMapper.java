package com.loctran.store.mappers;

import com.loctran.store.dtos.ProductDTO;
import com.loctran.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO productToProductDTO(Product product);
}
