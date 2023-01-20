package com.example.orderservice.service;


import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);
    Iterable<OrderEntity> getOrderByUserId(String UserId);

    OrderEntity getOrderByOrderId(String OrderId);
}
