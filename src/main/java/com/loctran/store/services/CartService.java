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
        return cartRepository.getCartWithItems(id).orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " not found"));
    }

    public CartDTO createCart() {
        Cart cart = new Cart();

        cartRepository.save(cart);

        return cartMapper.CartToCartDTO(cart);
    }

    public CartItemDTO addToCart(UUID id, CreateItemToCartRequest createItemToCartRequest) {
        Cart cart = getCartEntityById(id);
        Product product = productService.findProductEntityById(createItemToCartRequest.getProductId());

        CartItem cartItem = cart.getCarItemByProductId(product.getId());

        if( cartItem == null ) {
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(1);
            cart.getCartItems().add(newCartItem);
        } else{
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }

        cartRepository.save(cart);

        return cartMapper.CartItemToCartItemDTO(cartItem);
    }

    public CartItemDTO updateCart(UUID cartId, Long productId, Integer quantity){
        Cart cart = getCartEntityById(cartId);

        CartItem cartItem = cart.getCarItemByProductId(productId);

        if(cartItem == null){
            throw new ResourceNotFoundException("Item with productId " + productId + " not found");
        }

        return cartMapper.CartItemToCartItemDTO(cartItem);
    }

    public void deleteCartItem(UUID cartId, Long productId) {
        Cart cart = getCartEntityById(cartId);

        cart.removeCartItem(productId);

        cartRepository.save(cart);
    }


    public void deleteCartItem(UUID cartId) {
        Cart cart = getCartEntityById(cartId);

        cart.clear();
    }
}
