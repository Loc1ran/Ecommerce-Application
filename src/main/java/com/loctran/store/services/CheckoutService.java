package com.loctran.store.services;

import com.loctran.store.dtos.CheckoutRequest;
import com.loctran.store.dtos.CheckoutResponse;
import com.loctran.store.dtos.WebhookRequest;
import com.loctran.store.entities.*;
import com.loctran.store.exceptions.BadRequestException;
import com.loctran.store.exceptions.PaymentException;
import com.loctran.store.exceptions.ResourceNotFoundException;
import com.loctran.store.repositories.CartRepository;
import com.loctran.store.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthenticationServices authenticationServices;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final CartService cartService;

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest checkoutRequest) {
        Cart cart = cartRepository.getCartWithItems(checkoutRequest.getCartId()).orElseThrow(
                () -> new ResourceNotFoundException("Cart with id " + checkoutRequest.getCartId() + " not found")
        );

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is Empty");
        }

        User user = authenticationServices.getUser();

        Order order = Order.fromCart(user, cart);

        orderRepository.save(order);

        try {
            CheckoutSession session = paymentGateway.createCheckoutSession(order);

            cartService.clearCart(cart.getId());
            cartRepository.save(cart);

            return new CheckoutResponse(order.getOrderId(), session.getCheckoutUrl());
        } catch (PaymentException ex){
            orderRepository.delete(order);
            throw ex;
        }
    }

    public void handleWebhookEvent(WebhookRequest request){
        paymentGateway.parseWebhookRequest(request).ifPresent(paymentRequest -> {
            Order order = orderRepository.findById(paymentRequest.getOrderId()).orElseThrow();
            order.setStatus(paymentRequest.getOrderStatus());
            orderRepository.save(order);
        });
    }
}
