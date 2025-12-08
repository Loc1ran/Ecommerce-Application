package com.loctran.store.payments;

import com.loctran.store.exceptions.ErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(
            @Valid @RequestBody CheckoutRequest checkoutRequest) {
        CheckoutResponse response = checkoutService.checkout(checkoutRequest);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/webhook")
    public void handleWebHook(
            @RequestHeader Map<String, String> signature,
            @RequestBody String payload
    ){
        System.out.println("🔥🔥🔥 WEBHOOK HIT !!! 🔥🔥🔥");
        System.out.println("Headers = " + signature);
        System.out.println("Payload = " + payload);
        checkoutService.handleWebhookEvent(new WebhookRequest(signature, payload));
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
