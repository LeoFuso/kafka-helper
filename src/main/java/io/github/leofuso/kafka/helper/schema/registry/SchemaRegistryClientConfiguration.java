package io.github.leofuso.kafka.helper.schema.registry;

import io.confluent.kafka.schemaregistry.avro.*;
import io.confluent.kafka.schemaregistry.client.*;

import io.confluent.kafka.serializers.*;

import org.springframework.boot.autoconfigure.kafka.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
public class SchemaRegistryClientConfiguration {

    private final KafkaProperties properties;

    public SchemaRegistryClientConfiguration(final KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean
    SchemaRegistryClient registryClient() {

        final KafkaAvroDeserializerConfig config =
                new KafkaAvroDeserializerConfig(properties.buildConsumerProperties());

        final AvroSchemaProvider schemaProvider = new AvroSchemaProvider();
        final List<String> urls = config.getSchemaRegistryUrls();
        final int maxSchemaObject = config.getMaxSchemasPerSubject();
        final Map<String, Object> originals = config.originalsWithPrefix("");

        return SchemaRegistryClientFactory.newClient(
                urls,
                maxSchemaObject,
                Collections.singletonList(schemaProvider),
                originals,
                config.requestHeaders()
        );
    }
}
