package com.hotel.ordering_system.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class Order {
    private Integer id;
    private String tableNumber;
    private LocalDateTime orderTime;
    private String status;
    private double totalPrice;
    private List<OrderItem> items;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}