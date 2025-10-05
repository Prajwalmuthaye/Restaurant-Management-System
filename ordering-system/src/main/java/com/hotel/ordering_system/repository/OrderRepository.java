package com.hotel.ordering_system.repository;

import com.hotel.ordering_system.model.Order;
import com.hotel.ordering_system.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<Order> findAll();
    Optional<Order> findById(int id);

    void updateStatus(int orderId, String status);

    double sumTotalPriceBetweenDates(LocalDateTime start, LocalDateTime end);

    void update(Order order);
    void deleteOrderItemsByOrderId(int orderId);
    void saveOrderItem(OrderItem item);
}