package com.loctran.store.payments;

import com.loctran.store.entities.Order;
import com.loctran.store.entities.OrderStatus;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class StripePaymentGatewayTest {
    private final StripePaymentGateway gateway = new StripePaymentGateway();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(gateway, "websiteUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(gateway, "webhookSecretKey", "whsec_unit_test_only");
    }

    @Test
    void sessionIncludesPaymentIntentMetadataAndStableRetryKey() throws Exception {
        Order order = new Order();
        order.setOrderId(42L);
        try (MockedStatic<Session> sessions = mockStatic(Session.class)) {
            sessions.when(() -> Session.create(any(SessionCreateParams.class), any(RequestOptions.class)))
                    .thenAnswer(invocation -> {
                        SessionCreateParams params = invocation.getArgument(0);
                        RequestOptions options = invocation.getArgument(1);
                        assertEquals("42", params.getMetadata().get("order_id"));
                        assertEquals("42", params.getPaymentIntentData().getMetadata().get("order_id"));
                        assertEquals("checkout-order-42", options.getIdempotencyKey());
                        assertEquals(2, options.getMaxNetworkRetries());
                        Session session = new Session();
                        session.setUrl("https://checkout.stripe.com/test");
                        return session;
                    });
            assertEquals("https://checkout.stripe.com/test", gateway.createCheckoutSession(order).getCheckoutUrl());
        }
    }

    private WebhookRequest signedEvent(String type, String metadata) throws Exception {
        String payload = """
                {"id":"evt_test","object":"event","api_version":"%s","type":"%s",
                "data":{"object":{"id":"pi_test","object":"payment_intent","metadata":%s}}}
                """.formatted(Stripe.API_VERSION, type, metadata);
        long timestamp = Instant.now().getEpochSecond();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("whsec_unit_test_only".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = HexFormat.of().formatHex(mac.doFinal((timestamp + "." + payload).getBytes(StandardCharsets.UTF_8)));
        return new WebhookRequest(Map.of("stripe-signature", "t=" + timestamp + ",v1=" + signature), payload);
    }

    @Test
    void signedPaymentEventMapsToOrder() throws Exception {
        PaymentResult result = gateway.parseWebhookRequest(signedEvent("payment_intent.succeeded", "{\"order_id\":\"42\"}")).orElseThrow();
        assertEquals(42L, result.getOrderId());
        assertEquals(OrderStatus.PAID, result.getOrderStatus());
    }

    @Test
    void invalidSignatureIsRejected() {
        assertThrows(PaymentException.class, () -> gateway.parseWebhookRequest(
                new WebhookRequest(Map.of("stripe-signature", "t=1,v1=invalid"), "{}")));
    }

    @Test
    void absentSignatureIsRejected() {
        assertThrows(PaymentException.class, () -> gateway.parseWebhookRequest(new WebhookRequest(Map.of(), "{}")));
    }

    @Test
    void missingOrderMetadataIsRejected() throws Exception {
        WebhookRequest request = signedEvent("payment_intent.succeeded", "{}");
        assertThrows(PaymentException.class, () -> gateway.parseWebhookRequest(request));
    }

    @Test
    void unrelatedSignedEventIsIgnored() throws Exception {
        assertTrue(gateway.parseWebhookRequest(signedEvent("payment_intent.created", "{}")).isEmpty());
    }
}
