package com.loctran.store.services;

import com.loctran.store.dtos.OrderResponse;
import com.loctran.store.entities.Order;
import com.loctran.store.entities.User;
import com.loctran.store.mappers.OrderMapper;
import com.loctran.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final AuthenticationServices authenticationServices;
    private final OrderMapper orderMapper;
    private OrderRepository orderRepository;

    public List<OrderResponse> getOrders() {
        User customer = authenticationServices.getUser();

        List<Order> orders = orderRepository.getAllByCustomer(customer);

        return orders.stream().map(orderMapper::toDTO).toList();
    }
}
