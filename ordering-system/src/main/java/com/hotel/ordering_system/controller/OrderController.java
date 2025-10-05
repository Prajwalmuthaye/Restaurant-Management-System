package com.hotel.ordering_system.controller;

import com.hotel.ordering_system.model.Order;
import com.hotel.ordering_system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        try {
            // Ensure items are not null or empty before proceeding
            if (order.getItems() == null || order.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Order must contain at least one item.");
            }
            Order createdOrder = orderService.createOrder(order);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This catches invalid menu item IDs from the service layer
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}