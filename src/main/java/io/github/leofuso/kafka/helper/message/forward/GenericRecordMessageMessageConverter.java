package io.github.leofuso.kafka.helper.message.forward;

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
import org.springframework.web.*;

import org.apache.avro.*;
import org.apache.avro.generic.*;
import org.apache.logging.log4j.util.*;

import io.github.leofuso.kafka.helper.common.*;
import io.github.leofuso.kafka.helper.schema.registry.*;
import io.github.leofuso.record.mapper.*;
import io.micrometer.common.lang.*;

import com.fasterxml.jackson.databind.*;


public class GenericRecordMessageMessageConverter extends AbstractHttpMessageConverter<Message<GenericData.Record>> {

    private final SchemaRegistryService service;

    private final RecordMapper recordMapper;
    private final Schema.Parser schemaParser;

    GenericRecordMessageMessageConverter(final SchemaRegistryService registryService) {
        super(StandardCharsets.UTF_8, CommonHeaders.APPLICATION_AVRO_JSON_UTF8);
        service = Objects.requireNonNull(
                registryService,
                SchemaRegistryService.class.getSimpleName() + " [registryService] is required."
        );
        final RecordMapperFactory factory = RecordMapperFactory.get();
        recordMapper = factory.produce();
        schemaParser = new Schema.Parser();
    }

    private static Map<String, Object> resolveMessageHeaders(final HttpMessage message) {
        final HttpHeaders headers = message.getHeaders();
        return headers.entrySet()
                .stream()
                .map(entry -> {

                    final String key = entry.getKey();
                    return switch (key) {

                        case CommonHeaders.X_KEY -> new AbstractMap.SimpleEntry<>(KafkaHeaders.KEY, headers.getFirst(key));
                        case CommonHeaders.X_TOPIC -> new AbstractMap.SimpleEntry<>(KafkaHeaders.TOPIC, headers.getFirst(key));
                        case CommonHeaders.X_PARTITION -> {
                            final String partition = headers.getFirst(key);
                            yield new AbstractMap.SimpleEntry<>(key, partition == null ? null : Integer.valueOf(partition));
                        }
                        case CommonHeaders.X_TIMESTAMP -> {
                            final String timestamp = headers.getFirst(key);
                            yield new AbstractMap.SimpleEntry<>(key, timestamp == null ? null : Long.valueOf(timestamp));
                        }
                        default -> {
                            final String values = String.join(", ", entry.getValue());
                            yield new AbstractMap.SimpleEntry<>(key, values);
                        }
                    };
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings("DuplicatedCode")
    private Charset getContentTypeCharset(@Nullable MediaType contentType) {
        if (contentType != null) {
            Charset charset = contentType.getCharset();
            if (charset != null) {
                return charset;
            } else if (contentType.isCompatibleWith(CommonHeaders.APPLICATION_AVRO_JSON_UTF8)) {
                return StandardCharsets.UTF_8;
            }
        }

        final Charset defaultCharset = getDefaultCharset();
        return defaultCharset != null ? defaultCharset : StandardCharsets.UTF_8;
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }

    @Override
    protected Message<GenericData.Record> readInternal(final Class<? extends Message<GenericData.Record>> ignored,
                                                       final HttpInputMessage inputMessage) throws IOException {

        final HttpHeaders headers = inputMessage.getHeaders();
        if(!headers.containsKey(CommonHeaders.X_TOPIC)) {
            final String errorMessage = "A [%s] header is required.".formatted(CommonHeaders.X_TOPIC);
            final ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, detail, null);
        }

        final Map<String, Object> messageHeaders = resolveMessageHeaders(inputMessage);

        final String rawSchema = headers.getFirst(CommonHeaders.X_SCHEMA);
        final String schemaId = headers.getFirst(CommonHeaders.X_SCHEMA_ID);

        if (rawSchema == null && schemaId == null) {
            final String message = "At least one of [%s, %s] headers are required.".formatted(
                    CommonHeaders.X_SCHEMA,
                    CommonHeaders.X_SCHEMA_ID
            );
            final ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, detail, null);
        }

        final Schema schema = Optional.ofNullable(rawSchema)
                .map(schemaParser::parse)
                .or(() ->
                            Optional.ofNullable(schemaId)
                                    .map(id -> {
                                        final int value = Integer.parseInt(id);
                                        return service.accessById(value);
                                    })
                )
                .orElseThrow(() -> {
                    final String message = "Unable to match with a compatible Schema.";
                    final ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
                    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, detail, null);
                });

        final InputStream bodyStream = inputMessage.getBody();
        final String rawJson = StreamUtils.copyToString(bodyStream, getContentTypeCharset(headers.getContentType()));
        final GenericData.Record record = recordMapper.asGenericDataRecord(rawJson, schema);
        return new GenericMessage<>(record, messageHeaders);
    }

    @Override
    protected void writeInternal(final Message<GenericData.Record> source, final HttpOutputMessage outputMessage) throws IOException {

        final HttpHeaders httpHeaders = outputMessage.getHeaders();
        source.getHeaders()
                .forEach((key, value) -> httpHeaders
                        .compute(
                                key,
                                (ignoredKey, ignoredValue) -> List.of(String.valueOf(value))
                        )
                );

        final GenericData.Record payload = source.getPayload();
        final JsonNode node = recordMapper.asJsonNode(payload);
        final String stringified = node.asText();
        StreamUtils.copy(
                stringified != null ? stringified : Strings.EMPTY,
                getContentTypeCharset(httpHeaders.getContentType()),
                outputMessage.getBody()
        );
    }
}
