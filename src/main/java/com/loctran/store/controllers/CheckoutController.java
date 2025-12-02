package com.loctran.store.controllers;

import com.loctran.store.dtos.CheckoutRequest;
import com.loctran.store.dtos.CheckoutResponse;
import com.loctran.store.services.CheckoutService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public ResponseEntity<?> checkout(
            @Valid @RequestBody CheckoutRequest checkoutRequest) {
        try {
            CheckoutResponse response = checkoutService.checkout(checkoutRequest);
            return ResponseEntity.ok().body(response);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "Error creating a checkout session"
            );
        }
    }
}
