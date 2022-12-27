package io.github.leofuso.kafka.helper.schema.registry;

import java.io.*;
import java.lang.reflect.*;

import org.springframework.stereotype.*;

import io.confluent.kafka.schemaregistry.*;
import io.confluent.kafka.schemaregistry.client.*;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.*;

@Service
public class SchemaRegistryService {

    private final SchemaRegistryClient client;

    public SchemaRegistryService(final SchemaRegistryClient client) {
        this.client = client;
    }

    public ParsedSchema accessById(final int id) {
        try {
            return client.getSchemaById(id);
        } catch (IOException | RestClientException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    int register(final String subject, final ParsedSchema schema) throws RestClientException, IOException {
        return client.register(subject, schema);
    }

    int register(final String subject, final ParsedSchema schema, final int version) throws RestClientException, IOException {
        return client.register(subject, schema, version, 0);
    }
}
