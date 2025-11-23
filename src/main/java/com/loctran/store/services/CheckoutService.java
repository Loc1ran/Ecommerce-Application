package com.loctran.store.services;

import com.loctran.store.dtos.CheckoutRequest;
import com.loctran.store.dtos.CheckoutResponse;
import com.loctran.store.entities.*;
import com.loctran.store.exceptions.BadRequestException;
import com.loctran.store.exceptions.ResourceNotFoundException;
import com.loctran.store.repositories.CartRepository;
import com.loctran.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthenticationServices authenticationServices;
    private final OrderRepository orderRepository;

    public CheckoutResponse checkout(CheckoutRequest checkoutRequest) {
        Cart cart = cartRepository.getCartWithItems(checkoutRequest.getCartId()).orElseThrow(
                () -> new ResourceNotFoundException("Cart with id " + checkoutRequest.getCartId() + " not found")
        );

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is Empty");
        }

        User user = authenticationServices.getUser();

        Order order = Order.fromCart(user, cart);

        Order savedOrder = orderRepository.save(order);

        cart.clear();
        cartRepository.save(cart);

        return new CheckoutResponse(savedOrder.getOrderId());
    }
}
