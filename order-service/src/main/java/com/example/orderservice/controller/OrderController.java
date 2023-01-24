package com.example.orderservice.controller;


import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
@Slf4j
public class OrderController {


    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;
    OrderProducer orderProducer;
    @Autowired
    public OrderController(Environment env, OrderService orderService, KafkaProducer kafkaProducer, OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrdersByUserId(@PathVariable("userId") String userId){
        log.info("Before retrieve orders  date");
        Iterable<OrderEntity> orderlist = orderService.getOrderByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();

        orderlist.forEach(v->{
            result.add(new ModelMapper().map(v,ResponseOrder.class));
        });
        log.info("after retrieved orders  date");
        return ResponseEntity.status(HttpStatus.OK).body(result);


    }

    @GetMapping("/{orderId}/order")
    public ResponseEntity<ResponseOrder> getOrderByOrderId(@PathVariable("orderId") String orderId){
        OrderEntity order = orderService.getOrderByOrderId(orderId);

        ResponseOrder result = new ModelMapper().map(order,ResponseOrder.class);

/*

        order.forEach(v->{
            result.add(new ModelMapper().map(v,ResponseOrder.class));
        });
*/

        return ResponseEntity.status(HttpStatus.OK).body(result);


    }
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,@RequestBody RequestOrder requestOrder){
        log.info("Before retrieve orders  data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto orderDto = mapper.map(requestOrder,OrderDto.class);
        orderDto.setUserId(userId);

    //     jpa
        OrderDto CreateOrderDto = orderService.createOrder(orderDto);
        ResponseOrder result = mapper.map(CreateOrderDto,ResponseOrder.class);


        /* kafka */
//        orderDto.setOrderId(UUID.randomUUID().toString());
 //       orderDto.setTotalPrice(requestOrder.getQty()*requestOrder.getUnitPrice());

        /* send this order to the kafka */
        /*
        kafkaProducer.send("example-catalog-topic",orderDto);
        orderProducer.send("orders",orderDto);
        ResponseOrder result = mapper.map(orderDto,ResponseOrder.class);
*/
        log.info("after retrieve orders  data");
        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }
    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service on PORT %S",env.getProperty("local.server.port"));
    }

}
