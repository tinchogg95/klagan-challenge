package com.kLagan.challenge.infraestructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import com.kLagan.challenge.domain.model.AssetUploadEvent;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, AssetUploadEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AssetUploadEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, AssetUploadEvent> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "asset-upload-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        JsonDeserializer<AssetUploadEvent> deserializer = new JsonDeserializer<>(AssetUploadEvent.class);
        deserializer.addTrustedPackages("com.kLagan.challenge.domain.model");
        deserializer.setUseTypeHeaders(false);
        
        return new DefaultKafkaConsumerFactory<>(
            configProps,
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AssetUploadEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AssetUploadEvent> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        
        // Configure error handling with backoff
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            new FixedBackOff(2000, 3) // 2 seconds interval, max 3 attempts
        );
        factory.setCommonErrorHandler(errorHandler);
        
        return factory;
    }
}