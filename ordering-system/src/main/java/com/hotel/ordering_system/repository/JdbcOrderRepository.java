package com.hotel.ordering_system.repository;

import com.hotel.ordering_system.model.Order;
import com.hotel.ordering_system.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper to map a database row to an Order object (without its items)
    private final RowMapper<Order> orderRowMapper = (rs, rowNum) -> {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setTableNumber(rs.getString("table_number"));
        order.setOrderTime(rs.getTimestamp("order_time").toLocalDateTime());
        order.setStatus(rs.getString("status"));
        order.setTotalPrice(rs.getDouble("total_price"));
        return order;
    };

    // RowMapper to map a database row to an OrderItem object
    private final RowMapper<OrderItem> orderItemRowMapper = (rs, rowNum) -> {
        OrderItem item = new OrderItem();
        item.setId(rs.getInt("id"));
        item.setOrderId(rs.getInt("order_id"));
        item.setMenuItemId(rs.getInt("menu_item_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    };

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY order_time DESC";
        return jdbcTemplate.query(sql, orderRowMapper);
    }

    @Override
    public Optional<Order> findById(int id) {
        String orderSql = "SELECT * FROM orders WHERE id = ?";
        try {
            Order order = jdbcTemplate.queryForObject(orderSql, orderRowMapper, id);
            if (order != null) {
                String itemsSql = "SELECT * FROM order_items WHERE order_id = ?";
                List<OrderItem> items = jdbcTemplate.query(itemsSql, orderItemRowMapper, id);
                order.setItems(items);
            }
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // In JdbcOrderRepository.java
    @Override
    public void updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }

    @Override
    public double sumTotalPriceBetweenDates(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT SUM(total_price) FROM orders WHERE order_time BETWEEN ? AND ?";
        Double total = jdbcTemplate.queryForObject(sql, Double.class, start, end);
        return total == null ? 0.0 : total;
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE orders SET table_number = ?, total_price = ? WHERE id = ?";
        jdbcTemplate.update(sql, order.getTableNumber(), order.getTotalPrice(), order.getId());
    }

    @Override
    public void deleteOrderItemsByOrderId(int orderId) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        jdbcTemplate.update(sql, orderId);
    }

    @Override
    public void saveOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, item.getOrderId(), item.getMenuItemId(), item.getQuantity());
    }
}