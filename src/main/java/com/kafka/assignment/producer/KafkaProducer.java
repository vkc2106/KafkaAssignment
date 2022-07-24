package com.kafka.assignment.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaProducer<K, V> {

	@Autowired
	private KafkaTemplate<K, V> kafkaTemplate;

	@Value("kafka.topic.producer")
	private String topic;

	public void sendMessage(V message) {
		log.info(String.format("#### -> Producing message -> {}", message));
		this.kafkaTemplate.send(topic, message);
	}

	public void sendMessageWithCallback(V message) {
		ListenableFuture<SendResult<K, V>> future = kafkaTemplate.send(topic, message);

		future.addCallback(new ListenableFutureCallback<SendResult<K, V>>() {
			@Override
			public void onSuccess(SendResult<K, V> result) {
				log.info("Message [{}] delivered with offset {}", message, result.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				log.warn("Unable to deliver message [{}]. {}", message, ex.getMessage());
			}
		});
	}
	
	public <T> void sendMessage(String topic, V message) {
		log.info(String.format("#### -> Producing message -> {}", message));
		this.kafkaTemplate.send(topic, message);
	}
}
