package io.github.leofuso.kafka.helper.common.message.converter;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.kafka.support.*;
import org.springframework.messaging.*;
import org.springframework.messaging.support.*;
import org.springframework.util.*;

import org.apache.avro.*;
import org.apache.avro.generic.*;
import org.apache.kafka.common.utils.*;
import org.apache.logging.log4j.util.*;

import io.confluent.kafka.schemaregistry.*;
import io.confluent.kafka.schemaregistry.avro.*;
import io.github.leofuso.kafka.helper.common.*;
import io.github.leofuso.kafka.helper.schema.registry.*;
import io.github.leofuso.record.mapper.*;

import com.fasterxml.jackson.databind.*;


public class RecordMessageConverter extends AbstractHttpMessageConverter<Message<GenericData.Record>> {

    private final SchemaRegistryService service;
    private final RecordMapper recordMapper;

    RecordMessageConverter(final SchemaRegistryService registryService) {
        super(HeaderConstants.APPLICATION_AVRO);
        service = Objects.requireNonNull(
                registryService,
                SchemaRegistryService.class.getSimpleName() + " [registryService] is required."
        );
        final RecordMapperFactory factory = RecordMapperFactory.get();
        recordMapper = factory.produce();
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }

    @Override
    protected Message<GenericData.Record> readInternal(final Class<? extends Message<GenericData.Record>> ignored,
                                                       final HttpInputMessage inputMessage) throws IOException {

        final HttpHeaders headers = inputMessage.getHeaders();
        final Map<String, Object> headersMap = headers.entrySet()
                .stream()
                .map(entry -> {

                    final String key = entry.getKey();
                    return switch (key) {

                        case HeaderConstants.X_KEY -> Utils.mkEntry(KafkaHeaders.KEY, headers.getFirst(key));
                        case HeaderConstants.X_TOPIC -> Utils.mkEntry(KafkaHeaders.TOPIC, headers.getFirst(key));
                        case HeaderConstants.X_PARTITION -> {
                            final String partition = headers.getFirst(key);
                            yield Utils.mkEntry(key, partition == null ? null : Integer.valueOf(partition));
                        }
                        case HeaderConstants.X_TIMESTAMP -> {
                            final String timestamp = headers.getFirst(key);
                            yield Utils.mkEntry(key, timestamp == null ? null : Long.valueOf(timestamp));
                        }
                        default -> {
                            final String values = String.join(", ", entry.getValue());
                            yield Utils.mkEntry(key, values);
                        }
                    };
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        final String rawSchema = headers.getFirst(HeaderConstants.X_SCHEMA);
        final String schemaId = headers.getFirst(HeaderConstants.X_SCHEMA_ID);

        final ParsedSchema schema = Optional.ofNullable(rawSchema)
                .map(raw -> (ParsedSchema) new AvroSchema(raw))
                .or(() ->
                            Optional.ofNullable(schemaId)
                                    .map(id -> {
                                        final int value = Integer.parseInt(id);
                                        return service.accessById(value);
                                    })
                )
                .orElseThrow();

        final InputStream bodyStream = inputMessage.getBody();
        final String rawJson = StreamUtils.copyToString(bodyStream, StandardCharsets.UTF_8);
        final GenericData.Record record = recordMapper.asGenericDataRecord(rawJson, (Schema) schema.rawSchema());
        return new GenericMessage<>(record, headersMap);
    }

    @Override
    protected void writeInternal(final Message<GenericData.Record> source, final HttpOutputMessage outputMessage) throws IOException {

        final MessageHeaders headers = source.getHeaders();
        headers.forEach((key, o) -> {
            final HttpHeaders outputHeaders = outputMessage.getHeaders();
            final List<String> value = Collections.singletonList("%s".formatted(o));
            outputHeaders.addAll(key, value);
        });

        final GenericData.Record payload = source.getPayload();
        final JsonNode node = recordMapper.asJsonNode(payload);
        final String stringified = node.asText();
        StreamUtils.copy(stringified != null ? stringified : Strings.EMPTY, StandardCharsets.UTF_8, outputMessage.getBody());
    }
}
