package com.kafka.assignment.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KafkaProducerConfig {

	@Autowired
	private KafkaProps kafkaProps;

	@Bean
	public Map<String, Object> getProducerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		return props;
	}

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(getProducerConfigs());
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
		kafkaTemplate.setProducerListener(new ProducerListener<String, String>() {
			@Override
			public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
				log.info("ACK from ProducerListener message: {} offset:  {}", producerRecord.value(), recordMetadata.offset());
			}
		});
		return kafkaTemplate;
	}
}
