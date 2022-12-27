package io.github.leofuso.kafka.helper.common.message.converter;

import java.util.*;

import org.springframework.beans.factory.*;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.*;
import org.springframework.web.servlet.config.annotation.*;

import io.github.leofuso.kafka.helper.schema.registry.*;

@Configuration
public class RecordMessageConverterConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<SchemaRegistryService> service;

    public RecordMessageConverterConfiguration(final ObjectProvider<SchemaRegistryService> service) {
        this.service = Objects.requireNonNull(
                service,
                ObjectProvider.class.getSimpleName() + " [service] is required."
        );
    }

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        final SchemaRegistryService registryService = service.getIfAvailable();
        if (registryService == null) {
            return;
        }
        converters.add(new RecordMessageConverter(registryService));
    }
}
