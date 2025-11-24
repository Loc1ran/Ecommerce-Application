package com.loctran.store.dtos;

import com.loctran.store.entities.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;

}
