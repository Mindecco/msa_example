package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class OrderServiceimpl implements OrderService{

    OrderRepository orderRepository;


    public OrderServiceimpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty()*orderDto.getUnitPrice());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderEntity orderEntity = mapper.map(orderDto,OrderEntity.class);

        orderRepository.save(orderEntity);
        OrderDto resultValue = mapper.map(orderEntity,OrderDto.class);
        return resultValue;
    }

    @Override
    public Iterable<OrderEntity> getOrderByUserId(String UserId) {
        return orderRepository.findByUserId(UserId);
    }

    @Override
    public OrderEntity getOrderByOrderId(String OrderId) {
        return orderRepository.findByOrderId(OrderId);
    }
}
