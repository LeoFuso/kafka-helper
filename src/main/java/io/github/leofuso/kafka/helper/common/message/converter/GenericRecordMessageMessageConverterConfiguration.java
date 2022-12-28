package io.github.leofuso.kafka.helper.common.message.converter;

import java.util.*;

import org.springframework.beans.factory.*;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.*;
import org.springframework.web.servlet.config.annotation.*;

import io.github.leofuso.kafka.helper.schema.registry.*;

@Configuration
public class GenericRecordMessageMessageConverterConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<SchemaRegistryService> provider;

    public GenericRecordMessageMessageConverterConfiguration(final ObjectProvider<SchemaRegistryService> provider) {
        this.provider = Objects.requireNonNull(
                provider,
                ObjectProvider.class.getSimpleName() + " [provider] is required."
        );
    }

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        final SchemaRegistryService registryService = provider.getIfAvailable();
        if (registryService == null) {
            return;
        }
        converters.add(0,new GenericRecordMessageMessageConverter(registryService));
    }
}
