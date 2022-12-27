package io.github.leofuso.kafka.helper.common.message.converter;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import org.springframework.core.convert.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.util.*;

import org.apache.logging.log4j.util.*;

import io.micrometer.common.lang.*;


public class StringToObjectMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final ConversionService converter;

    StringToObjectMessageConverter(final ConversionService conversionService) {
        super(MediaType.ALL);
        converter = conversionService;
    }

    private Charset getContentTypeCharset(@Nullable MediaType contentType) {
        if (contentType != null) {
            Charset charset = contentType.getCharset();
            if (charset != null) {
                return charset;
            } else if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                return StandardCharsets.UTF_8;
            }
        }

        final Charset defaultCharset = getDefaultCharset();
        return defaultCharset != null ? defaultCharset : StandardCharsets.UTF_8;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return converter.canConvert(String.class, clazz);
    }

    @Override
    @Nullable
    protected Object readInternal(final Class<?> target, final HttpInputMessage inputMessage) throws IOException {

        final HttpHeaders headers = inputMessage.getHeaders();
        final Charset charset = getContentTypeCharset(headers.getContentType());

        final InputStream bodyStream = inputMessage.getBody();
        final String source = StreamUtils.copyToString(bodyStream, charset);
        return converter.convert(source, target);
    }

    @Override
    protected void writeInternal(final Object source, final HttpOutputMessage outputMessage) throws IOException {
        final HttpHeaders headers = outputMessage.getHeaders();
        final Charset charset = getContentTypeCharset(headers.getContentType());

        final String stringified = converter.convert(source, String.class);
        StreamUtils.copy(stringified != null ? stringified : Strings.EMPTY, charset, outputMessage.getBody());
    }
}
