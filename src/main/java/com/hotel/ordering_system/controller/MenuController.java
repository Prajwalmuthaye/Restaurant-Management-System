package com.hotel.ordering_system.controller;

import com.hotel.ordering_system.model.MenuItem;
import com.hotel.ordering_system.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        menuItem.setId(null);
        return new ResponseEntity<>(menuItemRepository.save(menuItem), HttpStatus.CREATED);
    }

    @GetMapping
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable int id) {
        return menuItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable int id, @RequestBody MenuItem menuItemDetails) {
        return menuItemRepository.findById(id)
                .map(menuItem -> {
                    menuItem.setName(menuItemDetails.getName());
                    menuItem.setDescription(menuItemDetails.getDescription());
                    menuItem.setPrice(menuItemDetails.getPrice());
                    menuItem.setCategory(menuItemDetails.getCategory());
                    menuItemRepository.update(menuItem);
                    return ResponseEntity.ok(menuItem);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable int id) {
        if (menuItemRepository.findById(id).isPresent()) {
            menuItemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}