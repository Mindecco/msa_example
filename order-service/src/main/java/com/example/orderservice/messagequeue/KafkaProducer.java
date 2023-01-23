package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaProducer {
    OrderRepository repository;
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(OrderRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDto send(String topic, OrderDto orderDto){
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString ="";
        try{
            jsonInString = mapper.writeValueAsString(orderDto);

        }catch (JsonProcessingException ex){
            ex.printStackTrace();;
        }
        kafkaTemplate.send(topic,jsonInString);
        log.info("Kafka Producer sent data fro mth " + orderDto);

        return orderDto;
    }

}

