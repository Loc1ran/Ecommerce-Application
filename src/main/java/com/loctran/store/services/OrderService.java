package com.loctran.store.services;

import com.loctran.store.dtos.OrderDTO;
import com.loctran.store.entities.Order;
import com.loctran.store.entities.User;
import com.loctran.store.exceptions.ResourceNotFoundException;
import com.loctran.store.mappers.OrderMapper;
import com.loctran.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final AuthenticationServices authenticationServices;
    private final OrderMapper orderMapper;
    private OrderRepository orderRepository;

    public List<OrderDTO> getOrders() {
        User customer = authenticationServices.getUser();

        List<Order> orders = orderRepository.getOrderByCustomer(customer);

        return orders.stream().map(orderMapper::toDTO).toList();
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findOrderById(id).orElseThrow(() -> new ResourceNotFoundException(
                "no order found with id " + id
        ));

        if(!order.verifyUserOrder(authenticationServices.getUser())) {
            throw new AccessDeniedException("No access to this order");
        }

        return orderMapper.toDTO(order);
    }
}
