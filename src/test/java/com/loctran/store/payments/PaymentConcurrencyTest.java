package com.loctran.store.payments;

import com.loctran.store.entities.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "SPRING_DATASOURCE_URL", matches = "jdbc:postgresql://127\\.0\\.0\\.1:[0-9]+/ecommerce_resume_bench")
class PaymentConcurrencyTest {
    @Autowired JdbcTemplate jdbc;
    @Autowired CheckoutService service;
    @MockitoBean PaymentGateway gateway;

    @Test
    void concurrentSuccessAndFailureAlwaysPreservePaidState() throws Exception {
        Long user = jdbc.queryForObject("INSERT INTO users(name,email,password) VALUES ('payment-test','payment-test@example.invalid','unused') RETURNING id", Long.class);
        try (var executor = Executors.newFixedThreadPool(2)) {
            for (int attempt = 0; attempt < 10; attempt++) {
                Long order = jdbc.queryForObject("INSERT INTO orders(customer_id,status,total_price) VALUES (?, 'PENDING', 10) RETURNING id", Long.class, user);
                doAnswer(invocation -> {
                    WebhookRequest event = invocation.getArgument(0);
                    return Optional.of(new PaymentResult(order, OrderStatus.valueOf(event.getPayload())));
                }).when(gateway).parseWebhookRequest(any());
                CountDownLatch start = new CountDownLatch(1);
                var paid = executor.submit(() -> {
                    start.await();
                    service.handleWebhookEvent(new WebhookRequest(Map.of(), "PAID"));
                    return null;
                });
                var failed = executor.submit(() -> {
                    start.await();
                    service.handleWebhookEvent(new WebhookRequest(Map.of(), "FAILED"));
                    return null;
                });
                start.countDown();
                paid.get(15, TimeUnit.SECONDS);
                failed.get(15, TimeUnit.SECONDS);
                assertEquals("PAID", jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, order));
            }
        } finally {
            jdbc.update("DELETE FROM users WHERE id = ?", user);
        }
    }
}
