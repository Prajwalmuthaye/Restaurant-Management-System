package com.hotel.ordering_system.service;


import com.hotel.ordering_system.model.MenuItem;
import com.hotel.ordering_system.model.Order;
import com.hotel.ordering_system.model.OrderItem;
import com.hotel.ordering_system.repository.MenuItemRepository;
import com.hotel.ordering_system.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

@Service
public class OrderService {


    @Autowired
    private OrderRepository orderRepository ;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Transactional
    public Order createOrder(Order order) {
        // 1. Calculate the total price based on current menu prices
        double totalPrice = order.getItems().stream()
                .mapToDouble(item -> {
                    MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID: " + item.getMenuItemId()));
                    return menuItem.getPrice() * item.getQuantity();
                })
                .sum();

        order.setTotalPrice(totalPrice);
        order.setStatus("PLACED");

        // 2. Save the parent Order record to get the auto-generated ID
        String orderSql = "INSERT INTO orders (table_number, status, total_price) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getTableNumber());
            ps.setString(2, order.getStatus());
            ps.setDouble(3, order.getTotalPrice());
            return ps;
        }, keyHolder);

        int orderId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        order.setId(orderId);

        // 3. Save each child OrderItem record using the new orderId
        String orderItemSql = "INSERT INTO order_items (order_id, menu_item_id, quantity) VALUES (?, ?, ?)";
        order.getItems().forEach(item ->
                jdbcTemplate.update(orderItemSql, orderId, item.getMenuItemId(), item.getQuantity())
        );

        return order;
    }
    @Transactional
    public void updateOrder(int orderId, Order updatedOrder) {
        // 1. Recalculate total price
        double totalPrice = updatedOrder.getItems().stream()
                .mapToDouble(item -> {
                    MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId()).orElseThrow();
                    return menuItem.getPrice() * item.getQuantity();
                })
                .sum();

        updatedOrder.setId(orderId);
        updatedOrder.setTotalPrice(totalPrice);

        // 2. Update the main order record
        orderRepository.update(updatedOrder);

        // 3. Delete all old items associated with this order
        orderRepository.deleteOrderItemsByOrderId(orderId);

        // 4. Insert the new list of items
        updatedOrder.getItems().forEach(item -> {
            item.setOrderId(orderId);
            orderRepository.saveOrderItem(item);
        });
    }
}