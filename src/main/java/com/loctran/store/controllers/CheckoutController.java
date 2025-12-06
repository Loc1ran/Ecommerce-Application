package com.loctran.store.controllers;

import com.loctran.store.dtos.CheckoutRequest;
import com.loctran.store.dtos.CheckoutResponse;
import com.loctran.store.entities.Order;
import com.loctran.store.entities.OrderStatus;
import com.loctran.store.exceptions.ErrorResponse;
import com.loctran.store.exceptions.PaymentException;
import com.loctran.store.repositories.OrderRepository;
import com.loctran.store.services.CheckoutService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(
            @Valid @RequestBody CheckoutRequest checkoutRequest) {
        CheckoutResponse response = checkoutService.checkout(checkoutRequest);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebHook(
            @RequestHeader("Stripe-Signature") String signature,
            @RequestBody String payload
    ){
        try {
            Event event = Webhook.constructEvent(payload, signature, webhookSecretKey);

            System.out.println(event.getType());

            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

            switch (event.getType()){
                case "payment_intent.succeeded" -> {
                    var paymentIntent = (PaymentIntent) stripeObject;

                    if(paymentIntent != null){
                        String orderId = paymentIntent.getMetadata().get("order_id");

                        Order order = orderRepository.findById(Long.valueOf(orderId)).orElseThrow();
                        order.setStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                    }
                }
                case "payment_intent.failed" -> {

                }
            }
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException(PaymentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
