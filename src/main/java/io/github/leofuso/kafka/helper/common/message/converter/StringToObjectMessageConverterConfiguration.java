package io.github.leofuso.kafka.helper.common.message.converter;

import java.util.*;

import org.springframework.beans.factory.*;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.*;
import org.springframework.http.converter.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class StringToObjectMessageConverterConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<? extends ConversionService> converter;

    public StringToObjectMessageConverterConfiguration(final ObjectProvider<? extends ConversionService> conversionService) {
        converter = conversionService;
    }

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        final ConversionService conversionService = converter.getIfAvailable();
        if(conversionService == null) {
            return;
        }
        converters.add(new StringToObjectMessageConverter(conversionService));
    }
}
