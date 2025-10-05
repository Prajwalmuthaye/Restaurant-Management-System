package com.hotel.ordering_system.model;

public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer menuItemId;
    private int quantity;

    // --- ADD THESE TWO TEMPORARY FIELDS ---
    // These fields are not in the database table.
    // They are used only to pass information to the 'add-order.html' page.
    private String name;
    private double price;

    // --- GETTERS AND SETTERS FOR ALL FIELDS ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Integer menuItemId) { this.menuItemId = menuItemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // --- GETTERS AND SETTERS FOR THE NEW FIELDS ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}