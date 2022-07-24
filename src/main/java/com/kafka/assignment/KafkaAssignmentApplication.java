package com.kafka.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import lombok.extern.slf4j.Slf4j;

@EnableKafka
@SpringBootApplication
@Slf4j
public class KafkaAssignmentApplication {

	public static void main(String[] args) {

		log.info("Applicatio starting");
		SpringApplication.run(KafkaAssignmentApplication.class, args);
		log.info("Application started");
	}

}
