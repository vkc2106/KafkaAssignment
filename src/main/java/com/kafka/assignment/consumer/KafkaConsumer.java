package com.kafka.assignment.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaConsumer {

	@KafkaListener(topics = "customer_topic, balance_topic", groupId = "assignment",  containerFactory = "kafkaListenerContainerFactory")
	public void onMessage(@Payload String message) {
		log.info("Received Message: " + message);
	}

	@KafkaListener(topics = "customer_topic")
	public void consumer(String data) {
		log.info("consumer topic" , data);
	}
	
	@KafkaListener(topics = "balance_topic")
	public void balance(String data) {
		log.info("balance tooic ", data);
	}
	
	@KafkaListener(topics = "customer_topic, balance_topic", groupId = "assignment",  containerFactory = "kafkaListenerContainerFactory")
	public void onMessage(ConsumerRecord consumerRecord) {
		log.info("Received ConsumerRecord Message {} :: {}" , consumerRecord.key(), consumerRecord.value());
	}

}
