package com.loctran.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User user;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade =  CascadeType.PERSIST)
    private Set<OrderItem> orderItems = new HashSet<>();

    public Order(User user, BigDecimal totalPrice, OrderStatus orderStatus) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.status = orderStatus;
    }

    public static Order fromCart(User user, Cart cart) {
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order(user, cart.getTotalPrice(), OrderStatus.PENDING);

        cart.getCartItems().forEach(item -> {
            order.addOrderItem(OrderItem.fromCartItem(item));
        });

        return order;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
