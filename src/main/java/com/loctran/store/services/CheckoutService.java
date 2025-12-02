package com.loctran.store.services;

import com.loctran.store.dtos.CheckoutRequest;
import com.loctran.store.dtos.CheckoutResponse;
import com.loctran.store.entities.*;
import com.loctran.store.exceptions.BadRequestException;
import com.loctran.store.exceptions.ResourceNotFoundException;
import com.loctran.store.repositories.CartRepository;
import com.loctran.store.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthenticationServices authenticationServices;
    private final OrderRepository orderRepository;

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest checkoutRequest) throws StripeException {
        Cart cart = cartRepository.getCartWithItems(checkoutRequest.getCartId()).orElseThrow(
                () -> new ResourceNotFoundException("Cart with id " + checkoutRequest.getCartId() + " not found")
        );

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is Empty");
        }

        User user = authenticationServices.getUser();

        Order order = Order.fromCart(user, cart);

        Order savedOrder = orderRepository.save(order);

        var builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getOrderId())
                .setCancelUrl(websiteUrl + "/checkout-cancel");

        order.getOrderItems().forEach(item -> {
            var lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(Long.valueOf(item.getQuantity()))
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmountDecimal(item.getUnitPrice())
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(item.getProduct().getName()).build()
                                    ).build()
                    ).build();
            builder.addLineItem(lineItem);
        });

        try {
            Session session = Session.create(builder.build());

            cart.clear();
            cartRepository.save(cart);

            return new CheckoutResponse(savedOrder.getOrderId(), session.getUrl());
        } catch (StripeException e){
            orderRepository.delete(savedOrder);
            throw e;
        }
    }
}
