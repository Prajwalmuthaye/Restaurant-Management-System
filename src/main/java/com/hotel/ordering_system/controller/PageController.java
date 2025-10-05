package com.hotel.ordering_system.controller;

import com.hotel.ordering_system.model.MenuItem;
import com.hotel.ordering_system.model.Order;
import com.hotel.ordering_system.model.OrderItem;
import com.hotel.ordering_system.repository.MenuItemRepository;
import com.hotel.ordering_system.repository.OrderRepository;
import com.hotel.ordering_system.service.OrderService;
import com.hotel.ordering_system.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping("/")
    public String showHomePage() {
        return "HomePage";
    }

    @GetMapping("/menu")
    public String showMenuList(Model model) {
        model.addAttribute("menuItems", menuItemRepository.findAll());
        return "menu-list"; // Renders menu-list.html
    }

    /**
     * Displays the form to add a new menu item.
     */
    @GetMapping("/menu/new")
    public String showNewItemForm(Model model) {
        // Create a new MenuItem object to bind to the form
        MenuItem menuItem = new MenuItem();
        model.addAttribute("menuItem", menuItem);
        return "add-menu-item"; // Renders add-menu-item.html
    }

    /**
     * Processes the form submission for saving a new menu item.
     */
    @PostMapping("/menu/save")
    public String saveMenuItem(@ModelAttribute("menuItem") MenuItem menuItem) {
        menuItemRepository.save(menuItem);
        return "redirect:/menu"; // Redirects to the menu list page
    }

    /**
     * Displays the form to edit an existing menu item.
     */
    @GetMapping("/menu/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu item Id:" + id));
        model.addAttribute("menuItem", menuItem);
        return "edit-menu-item"; // Renders edit-menu-item.html
    }

    /**
     * Processes the form submission for updating a menu item.
     */
    @PostMapping("/menu/update/{id}")
    public String updateMenuItem(@PathVariable int id, @ModelAttribute("menuItem") MenuItem menuItem) {
        menuItem.setId(id); // Ensure the ID is set for the update
        menuItemRepository.update(menuItem);
        return "redirect:/menu"; // Redirects to the menu list page
    }

    /**
     * Handles the deletion of a menu item.
     */
    @GetMapping("/menu/delete/{id}")
    public String deleteMenuItem(@PathVariable int id) {
        menuItemRepository.deleteById(id);
        return "redirect:/menu"; // Redirects to the menu list page
    }


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    /**
     * Displays the page with a list of all orders.
     */
    @GetMapping("/orders")
    public String showOrderList(Model model) {
        // 1. Fetch all orders from the database
        List<Order> allOrders = orderRepository.findAll();

        // 2. Use Java Streams to filter orders into two separate lists
        List<Order> unpaidOrders = allOrders.stream()
                .filter(order -> !order.getStatus().equalsIgnoreCase("PAID"))
                .toList();

        List<Order> paidOrders = allOrders.stream()
                .filter(order -> order.getStatus().equalsIgnoreCase("PAID"))
                .toList();

        // 3. Add both lists to the model so the template can access them
        model.addAttribute("unpaidOrders", unpaidOrders);
        model.addAttribute("paidOrders", paidOrders);

        return "order-list";
    }

    @GetMapping("/orders/new")
    public String showNewOrderForm(Model model) {
        Order order = new Order();
        // Get all menu items and transform them into order items with quantity 0
        List<OrderItem> potentialItems = menuItemRepository.findAll().stream()
                .map(menuItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setMenuItemId(menuItem.getId());
                    // We pass the name and price via the model, not part of the object
                    // But let's create a temporary MenuItem in OrderItem to hold these details for the view.
                    orderItem.setName(menuItem.getName()); // Temporary holder for name
                    orderItem.setPrice(menuItem.getPrice()); // Temporary holder for price
                    orderItem.setQuantity(0);
                    return orderItem;
                }).toList();

        order.setItems(potentialItems);
        model.addAttribute("order", order);
        return "add-order";
    }

    /**
     * Processes the new order form submission.
     */
    @PostMapping("/orders/save")
    public String saveOrder(@ModelAttribute("order") Order order) {
        // Filter out items where the quantity is 0
        List<OrderItem> itemsToOrder = order.getItems().stream()
                .filter(item -> item.getQuantity() > 0)
                .toList();

        if (itemsToOrder.isEmpty()) {
            // Optional: Redirect with an error if no items were selected
            return "redirect:/orders/new?error=No items selected";
        }

        order.setItems(itemsToOrder);
        orderService.createOrder(order);
        return "redirect:/orders";
    }
    @GetMapping("/orders/bill/{id}")
    public String showBillPage(@PathVariable int id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));

        // We need to enrich the order items with names and prices
        order.getItems().forEach(item -> {
            menuItemRepository.findById(item.getMenuItemId()).ifPresent(menuItem -> {
                item.setName(menuItem.getName());
                item.setPrice(menuItem.getPrice());
            });
        });

        model.addAttribute("order", order);
        return "bill-details";
    }

    @PostMapping("/orders/pay/{id}")
    public String markOrderAsPaid(@PathVariable int id) {
        orderRepository.updateStatus(id, "PAID");
        return "redirect:/orders";
    }

    @Autowired
    private ReportService reportService;

    @GetMapping("/reports")
    public String showReportsPage(Model model) {
        model.addAttribute("dailyTotal", reportService.getDailySales());
        model.addAttribute("weeklyTotal", reportService.getWeeklySales());
        model.addAttribute("monthlyTotal", reportService.getMonthlySales());
        return "reports";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/menuCard")
    public String showAllMenuList(Model model) {
        model.addAttribute("menuItems", menuItemRepository.findAll()); // Or use your MenuService
        return "menu-cards"; // Change this from "menu-list" to "menu-cards"
    }

    @GetMapping("/orders/edit/{id}")
    public String showEditOrderForm(@PathVariable int id, Model model) {
        // Fetch the existing order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));

        // Fetch all menu items
        List<MenuItem> allMenuItems = menuItemRepository.findAll();

        // Create a map of existing items for easy lookup
        Map<Integer, Integer> existingItemsMap = order.getItems().stream()
                .collect(Collectors.toMap(OrderItem::getMenuItemId, OrderItem::getQuantity));

        // Create a full list of potential items, pre-filling quantities from the existing order
        List<OrderItem> potentialItems = allMenuItems.stream()
                .map(menuItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setMenuItemId(menuItem.getId());
                    orderItem.setName(menuItem.getName());
                    orderItem.setPrice(menuItem.getPrice());
                    // Set existing quantity or default to 0
                    orderItem.setQuantity(existingItemsMap.getOrDefault(menuItem.getId(), 0));
                    return orderItem;
                }).toList();

        order.setItems(potentialItems);
        model.addAttribute("order", order);
        return "edit-order";
    }

    @PostMapping("/orders/update/{id}")
    public String updateOrder(@PathVariable int id, @ModelAttribute("order") Order order) {
        // Filter out items where the quantity is 0
        List<OrderItem> itemsToOrder = order.getItems().stream()
                .filter(item -> item.getQuantity() > 0)
                .toList();

        order.setItems(itemsToOrder);
        orderService.updateOrder(id, order);
        return "redirect:/orders";
    }
}