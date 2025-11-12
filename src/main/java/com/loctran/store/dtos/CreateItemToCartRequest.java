package com.loctran.store.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateItemToCartRequest {
    @NotNull
    private Long productId;
}
