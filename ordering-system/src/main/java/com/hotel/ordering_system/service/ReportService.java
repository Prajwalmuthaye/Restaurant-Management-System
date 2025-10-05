package com.hotel.ordering_system.service;

import com.hotel.ordering_system.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    public double getDailySales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return orderRepository.sumTotalPriceBetweenDates(startOfDay, now);
    }

    public double getWeeklySales() {
        // Correct way to get start of the week
        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        // Correct way to get end of the week
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).with(LocalTime.MAX);
        return orderRepository.sumTotalPriceBetweenDates(startOfWeek, endOfWeek);
    }

    public double getMonthlySales() {
        // Correct way to get start of the month
        LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        // Correct way to get end of the month
        LocalDate lastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime endOfMonth = LocalDateTime.of(lastDay, LocalTime.MAX); // Combine date and time
        return orderRepository.sumTotalPriceBetweenDates(startOfMonth, endOfMonth);
    }
}