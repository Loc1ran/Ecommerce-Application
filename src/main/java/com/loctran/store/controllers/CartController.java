package com.loctran.store.controllers;

import com.loctran.store.dtos.CartDTO;
import com.loctran.store.dtos.CartItemDTO;
import com.loctran.store.dtos.CreateItemToCartRequest;
import com.loctran.store.services.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/carts")
public class CartController {
    private CartService cartService;

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable UUID cartId) {
        CartDTO cartDTO = cartService.getCartById(cartId);

        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping
    public ResponseEntity<CartDTO> createdCart() {
        CartDTO cartDTO = cartService.createCart();

        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDTO> addToCart(
            @PathVariable UUID cartId,
            @RequestBody CreateItemToCartRequest createItemToCartRequest) {
        CartItemDTO cartItemDTO = cartService.addToCart(cartId, createItemToCartRequest);

        return ResponseEntity.ok(cartItemDTO);
    }

}
