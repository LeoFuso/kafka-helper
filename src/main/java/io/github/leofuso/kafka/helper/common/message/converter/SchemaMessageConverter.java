package io.github.leofuso.kafka.helper.common.message.converter;

import java.io.*;
import java.nio.charset.*;

import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.util.*;

import org.apache.avro.*;
import org.apache.logging.log4j.util.*;

import io.github.leofuso.kafka.helper.common.*;
import io.micrometer.common.lang.*;


public class SchemaMessageConverter extends AbstractHttpMessageConverter<Schema> {

    private final Schema.Parser parser;

    SchemaMessageConverter() {
        super(StandardCharsets.UTF_8, CommonHeaders.APPLICATION_SCHEMA_AVRO_JSON_UTF8);
        parser = new Schema.Parser();
    }

    @SuppressWarnings("DuplicatedCode")
    private Charset getContentTypeCharset(@Nullable MediaType contentType) {
        if (contentType != null) {
            Charset charset = contentType.getCharset();
            if (charset != null) {
                return charset;
            } else if (contentType.isCompatibleWith(CommonHeaders.APPLICATION_SCHEMA_AVRO_JSON_UTF8)) {
                return StandardCharsets.UTF_8;
            }
        }

        final Charset defaultCharset = getDefaultCharset();
        return defaultCharset != null ? defaultCharset : StandardCharsets.UTF_8;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return Schema.class.isAssignableFrom(clazz);
    }

    @Override
    protected Schema readInternal(final Class<? extends Schema> target, final HttpInputMessage inputMessage) throws IOException {
        final HttpHeaders headers = inputMessage.getHeaders();
        final Charset charset = getContentTypeCharset(headers.getContentType());

        final InputStream bodyStream = inputMessage.getBody();
        final String source = StreamUtils.copyToString(bodyStream, charset);
        return parser.parse(source);
    }

    @Override
    protected void writeInternal(final Schema source, final HttpOutputMessage outputMessage) throws IOException {
        final HttpHeaders headers = outputMessage.getHeaders();
        final Charset charset = getContentTypeCharset(headers.getContentType());

        final String stringified = source.toString(true);
        StreamUtils.copy(stringified != null ? stringified : Strings.EMPTY, charset, outputMessage.getBody());
    }
}
