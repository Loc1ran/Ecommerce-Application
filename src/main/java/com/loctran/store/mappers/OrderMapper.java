package com.loctran.store.mappers;

import com.loctran.store.dtos.OrderResponse;
import com.loctran.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toDTO(Order order);
}
