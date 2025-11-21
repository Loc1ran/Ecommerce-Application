package com.loctran.store.services;

import com.loctran.store.dtos.CheckoutRequest;
import com.loctran.store.dtos.CheckoutResponse;
import com.loctran.store.entities.Cart;
import com.loctran.store.entities.Order;
import com.loctran.store.entities.OrderItem;
import com.loctran.store.entities.OrderStatus;
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

        Order order = new Order();
        order.setUser(authenticationServices.getUser());
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);

        cart.getCartItems().forEach(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(item.getTotalPrice());
            orderItem.setUnitPrice(item.getProduct().getPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        });

        cart.clear();
        orderRepository.save(order);

        return new CheckoutResponse(order.getOrderId());
    }
}
