package io.github.leofuso.kafka.helper.schema.registry;

import io.confluent.kafka.schemaregistry.*;
import io.confluent.kafka.schemaregistry.client.*;

import org.springframework.stereotype.*;

@Service
public class SchemaRegistryService {

    private final SchemaRegistryClient client;

    public SchemaRegistryService(final SchemaRegistryClient client) {
        this.client = client;
    }

    public String accessById(int id) throws Exception {
        final ParsedSchema schema = client.getSchemaById(id);
        return schema.canonicalString();
    }

    public int register(final String subject, ParsedSchema schema) throws Exception {
        return client.register(subject, schema);
    }

    public int register(final String subject, ParsedSchema schema, int version) throws Exception {
        return client.register(subject, schema, version, 0);
    }
}
