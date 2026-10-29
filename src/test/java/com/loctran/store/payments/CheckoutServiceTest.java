package com.loctran.store.payments;

import com.loctran.store.auth.AuthenticationServices;
import com.loctran.store.entities.Order;
import com.loctran.store.entities.OrderStatus;
import com.loctran.store.repositories.CartRepository;
import com.loctran.store.repositories.OrderRepository;
import com.loctran.store.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {
    private final OrderRepository orders = mock(OrderRepository.class);
    private final PaymentGateway gateway = mock(PaymentGateway.class);
    private final WebhookRequest request = new WebhookRequest(Map.of(), "{}");
    private final CheckoutService service = new CheckoutService(mock(CartRepository.class),
            mock(AuthenticationServices.class), orders, gateway, mock(CartService.class));
    private Order order;

    @BeforeEach
    void setup() {
        order = new Order();
        order.setStatus(OrderStatus.PENDING);
        when(orders.findForPaymentUpdate(42L)).thenReturn(Optional.of(order));
    }

    private void event(OrderStatus status) {
        when(gateway.parseWebhookRequest(request)).thenReturn(Optional.of(new PaymentResult(42L, status)));
        service.handleWebhookEvent(request);
    }

    @Test
    void duplicateSuccessOnlyWritesOnce() {
        event(OrderStatus.PAID);
        event(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orders, times(1)).save(order);
    }

    @Test
    void lateFailureCannotUndoPayment() {
        event(OrderStatus.PAID);
        event(OrderStatus.FAILED);
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orders, times(1)).save(order);
    }

    @Test
    void successfulRetryCanRecoverFailedPayment() {
        event(OrderStatus.FAILED);
        event(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orders, times(2)).save(order);
    }

    @Test
    void unhandledEventDoesNotTouchOrders() {
        when(gateway.parseWebhookRequest(request)).thenReturn(Optional.empty());
        service.handleWebhookEvent(request);
        verifyNoInteractions(orders);
    }

    @Test
    void missingOrderFailsInsteadOfAcknowledgingLostUpdate() {
        when(orders.findForPaymentUpdate(42L)).thenReturn(Optional.empty());
        assertThrows(PaymentException.class, () -> event(OrderStatus.PAID));
        verify(orders, never()).save(any());
    }
}
