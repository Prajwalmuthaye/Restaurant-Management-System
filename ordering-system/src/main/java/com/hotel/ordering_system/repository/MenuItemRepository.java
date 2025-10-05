package com.hotel.ordering_system.repository;

import com.hotel.ordering_system.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    List<MenuItem> findAll();
    Optional<MenuItem> findById(int id);
    MenuItem save(MenuItem menuItem);
    int update(MenuItem menuItem);
    void deleteById(int id);
}