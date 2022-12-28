package io.github.leofuso.kafka.helper.schema.registry;

import java.util.*;

import org.springframework.context.annotation.*;
import org.springframework.http.converter.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class SchemaMessageConverterConfiguration implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(0,new SchemaMessageConverter());
    }
}
