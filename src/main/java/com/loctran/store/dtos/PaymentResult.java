package com.loctran.store.dtos;

import com.loctran.store.entities.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResult {
    Long orderId;
    OrderStatus orderStatus;
}
