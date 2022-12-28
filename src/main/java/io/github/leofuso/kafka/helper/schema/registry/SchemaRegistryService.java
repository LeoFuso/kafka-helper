package io.github.leofuso.kafka.helper.schema.registry;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.*;

import org.apache.avro.*;

import io.confluent.kafka.schemaregistry.*;
import io.confluent.kafka.schemaregistry.avro.*;
import io.confluent.kafka.schemaregistry.client.*;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.*;

@Service
public class SchemaRegistryService {

    private final SchemaRegistryClient client;

    SchemaRegistryService(final SchemaRegistryClient client) {
        this.client = Objects.requireNonNull(client, SchemaRegistryClient.class.getSimpleName() + " [client] is required.");
    }

    private static <T> T handleException(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (final RestClientException e) {
            final HttpStatusCode code = HttpStatusCode.valueOf(e.getStatus());
            final ProblemDetail detail = ProblemDetail.forStatusAndDetail(code, e.getMessage());
            throw new ErrorResponseException(code, detail, e);
        } catch (final Exception e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    public Schema accessById(final int id) {
        return handleException(() -> {
            final ParsedSchema parsedSchema = client.getSchemaById(id);
            return (Schema) parsedSchema.rawSchema();
        });
    }

    Integer register(final String subject, final Schema schema) {
        return handleException(() -> client.register(subject, new AvroSchema(schema)));
    }

    Integer register(final String subject, final Schema schema, final int version) {
        return handleException(() -> client.register(subject, new AvroSchema(schema), version, 0));
    }
}
