package com.loctran.store.mappers;

import com.loctran.store.dtos.CartDTO;
import com.loctran.store.dtos.CartItemDTO;
import com.loctran.store.entities.Cart;
import com.loctran.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDTO  CartToCartDTO(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDTO  CartItemToCartItemDTO(CartItem cartItem);
}
