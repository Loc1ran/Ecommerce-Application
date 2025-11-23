package com.loctran.store.controllers;

import com.loctran.store.dtos.*;
import com.loctran.store.services.CartService;
import jakarta.validation.Valid;
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
            @Valid @RequestBody CreateItemToCartRequest createItemToCartRequest) {
        CartItemDTO cartItemDTO = cartService.addToCart(cartId, createItemToCartRequest);

        return ResponseEntity.ok(cartItemDTO);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartItemDTO> updateCart(
            @PathVariable UUID cartId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemQuantity quantity) {
        CartItemDTO cartItemDTO = cartService.updateCart(cartId, productId, quantity.getQuantity());

        return ResponseEntity.ok(cartItemDTO);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> deleteCartProduct(
            @PathVariable UUID cartId, @PathVariable Long productId) {
        cartService.clearCart(cartId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable UUID cartId) {
        cartService.clearCart(cartId);

        return  ResponseEntity.ok().build();
    }


}
