package com.loctran.store.mappers;

import com.loctran.store.dtos.ProductDTO;
import com.loctran.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO productToProductDTO(Product product);

    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "id", ignore = true)
    void updateProduct(ProductDTO productDTO, @MappingTarget Product product);
}

