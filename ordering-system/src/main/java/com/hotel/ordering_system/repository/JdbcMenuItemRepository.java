package com.hotel.ordering_system.repository;


import com.hotel.ordering_system.model.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMenuItemRepository implements MenuItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static class MenuItemRowMapper implements RowMapper<MenuItem> {
        @Override
        public MenuItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MenuItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("category")
            );
        }
    }

    @Override
    public List<MenuItem> findAll() {
        return jdbcTemplate.query("SELECT * FROM menu_items", new MenuItemRowMapper());
    }

    @Override
    public Optional<MenuItem> findById(int id) {
        try {
            MenuItem menuItem = jdbcTemplate.queryForObject("SELECT * FROM menu_items WHERE id = ?", new MenuItemRowMapper(), id);
            return Optional.ofNullable(menuItem);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        jdbcTemplate.update("INSERT INTO menu_items (name, description, price, category) VALUES (?, ?, ?, ?)",
                menuItem.getName(), menuItem.getDescription(), menuItem.getPrice(), menuItem.getCategory());
        return menuItem;
    }

    @Override
    public int update(MenuItem menuItem) {
        return jdbcTemplate.update("UPDATE menu_items SET name = ?, description = ?, price = ?, category = ? WHERE id = ?",
                menuItem.getName(), menuItem.getDescription(), menuItem.getPrice(), menuItem.getCategory(), menuItem.getId());
    }

    @Override
    public void deleteById(int id) {
        jdbcTemplate.update("DELETE FROM menu_items WHERE id = ?", id);
    }
}