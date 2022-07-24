package com.kafka.assignment.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;

import com.kafka.assignment.config.KafkaProps;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

public class KafkaStreamsAssignment {
	
	@Autowired
	private KafkaProps kafkaProps;
	
	public Properties getConfigs() {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaProps.getApplicationId() );
		props.put(StreamsConfig.CLIENT_ID_CONFIG, kafkaProps.getClientId());
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
		props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaProps.getClientId());
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
	    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	    props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
		return props;
	}
	
	
	public void processRecord() {
		try {
			final Serde<String> keySerde = Serdes.String();
			final Serde<GenericRecord> valueSerde = Serdes.serdeFrom(GenericRecord.class);

			final StreamsBuilder builder = new StreamsBuilder();
			final KStream<String, GenericRecord> customers = builder.stream("customer");
			
			final KStream<String, GenericRecord> customersByAccountId = customers
			  .map((dummy, record) ->
			    new KeyValue<>(record.get("accountId").toString(), record));
			
			final KTable<String, GenericRecord> balances = builder.table("balance");

			final KTable<String, String> balanceByAccountId = balances.mapValues(record ->
			  record.get("accountId").toString());

			final InputStream
			  kafkaStreams =
					  KafkaStreamsAssignment.class.getClassLoader()
			    .getResourceAsStream("avro/customeBalance.avsc");
			final Schema schema = new Schema.Parser().parse(kafkaStreams);

			final KStream<String, GenericRecord> customerBalances = customersByAccountId
			  .leftJoin(balanceByAccountId, (customer, balance) -> {
			    final GenericRecord viewRegion = new GenericData.Record(schema);
			    viewRegion.put("accountId", customer.get("accountId"));
			    viewRegion.put("customerId", customer.get("customerId"));
			    viewRegion.put("phone", customer.get("phone"));
			    viewRegion.put("balance", balance);
			    return viewRegion;
			  })
			  //.windowedBy(TimeWindows.of(Duration.ofMinutes(5)).advanceBy(Duration.ofMinutes(1)))
			  ;
			
			customerBalances.to("customerBalance", Produced.with(keySerde, valueSerde));

			final KafkaStreams streams = new KafkaStreams(builder.build(), getConfigs());
			streams.cleanUp();
			streams.start();

			// Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
			Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
		} catch (StreamsException | IllegalArgumentException | IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
	

}
