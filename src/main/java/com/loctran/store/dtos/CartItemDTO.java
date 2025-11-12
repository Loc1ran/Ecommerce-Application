package com.loctran.store.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private CartProductDTO product;
    private Integer quantity;
    private BigDecimal totalPrice;
}
