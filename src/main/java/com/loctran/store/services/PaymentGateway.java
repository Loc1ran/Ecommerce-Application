package com.loctran.store.services;

import com.loctran.store.dtos.PaymentResult;
import com.loctran.store.dtos.WebhookRequest;
import com.loctran.store.entities.Order;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);
    Optional<PaymentResult> parseWebhookRequest(WebhookRequest webhookRequest);
}
