package com.loctran.store.services;

import com.loctran.store.dtos.CartDTO;
import com.loctran.store.dtos.CartItemDTO;
import com.loctran.store.dtos.CreateItemToCartRequest;
import com.loctran.store.entities.Cart;
import com.loctran.store.entities.CartItem;
import com.loctran.store.entities.Product;
import com.loctran.store.exceptions.ResourceNotFoundException;
import com.loctran.store.mappers.CartMapper;
import com.loctran.store.repositories.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {
    private final CartMapper cartMapper;
    private final ProductService productService;
    private CartRepository cartRepository;

    public CartDTO getCartById(UUID id) {
        Cart cart = getCartEntityById(id);

        return cartMapper.CartToCartDTO(cart);
    }

    public Cart getCartEntityById(UUID id) {
        return cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " not found"));
    }

    public CartDTO createCart() {
        Cart cart = new Cart();

        cartRepository.save(cart);

        return cartMapper.CartToCartDTO(cart);
    }

    public CartItemDTO addToCart(UUID id, CreateItemToCartRequest createItemToCartRequest) {
        Cart cart = getCartEntityById(id);
        Product product = productService.findProductEntityById(createItemToCartRequest.getProductId());

        CartItem updatedCartItem = cart.getCartItems().stream().filter(
                item -> item.getProduct().getId().equals(product.getId())
        ).findFirst().map(
                item -> {
                    item.setQuantity(item.getQuantity() + 1);
                    return item;
                }).orElseGet(() -> {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setQuantity(1);
                cartItem.setCart(cart);
                cart.getCartItems().add(cartItem);
                return cartItem;
            });

        cartRepository.save(cart);

        return cartMapper.CartItemToCartItemDTO(updatedCartItem);
    }
}
