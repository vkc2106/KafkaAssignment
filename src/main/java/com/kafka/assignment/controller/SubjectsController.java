package com.kafka.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.assignment.dto.Balance;
import com.kafka.assignment.dto.Customer;
import com.kafka.assignment.producer.KafkaProducer;

@RestController
@RequestMapping(value = "/subjects")
public class SubjectsController {
	
	@SuppressWarnings("rawtypes")
	@Autowired
    private KafkaProducer kafkaProducer;
	
	@Value("kafka.topic.customer")
	private String customerTopic;
	
	@Value("kafka.topic.balance")
	private String balanceTopic;

	@PostMapping("/Customer-{id}/versions/latest")
    public String customerMessage(@PathVariable String id) {
		
		Customer customer = new Customer();
		customer.withCustomerId(id).withName("mehryar").withPhoneNumber("888-888-8888").withAccountId("b");
		sendMessage(customerTopic, customer);
        return "sent to Customer topic";
    }
    
	@PostMapping("/Balance-{id}/versions/latest")
    public String balanceMessage(@PathVariable String id) {
		
		Balance balance = new Balance();
		balance.withBalanceId(id).withAccountId("b").withBalance(20.23f);
		sendMessage(balanceTopic, balance);
        return "sent to Balance topic";
    }
	
	@PostMapping("/CustomerBalance-{id}/versions/latest")
    public String customerBalanceMessage(@PathVariable String id) {
        return "sent to CustomerBalance topic";
    }
	
	private <T> void sendMessage(String topic, T message) {
		this.kafkaProducer.sendMessage(topic, message);
	}
	
}
