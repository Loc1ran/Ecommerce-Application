package com.loctran.store.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CartDTO {
    private UUID id;
    private List<CartItemDTO> cartItems  = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
}
