package com.loctran.store.mappers;

import com.loctran.store.dtos.OrderDTO;
import com.loctran.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);
}
