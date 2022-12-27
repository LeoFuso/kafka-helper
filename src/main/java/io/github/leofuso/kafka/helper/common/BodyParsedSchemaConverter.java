package io.github.leofuso.kafka.helper.common;

import java.util.*;

import org.springframework.core.convert.*;
import org.springframework.core.convert.converter.*;
import org.springframework.stereotype.*;

import io.confluent.kafka.schemaregistry.*;
import io.confluent.kafka.schemaregistry.avro.*;

@Component
public class BodyParsedSchemaConverter implements GenericConverter {

    private final Set<ConvertiblePair> pair = Set.of(
            new ConvertiblePair(String.class, ParsedSchema.class),
            new ConvertiblePair(ParsedSchema.class, String.class)
    );

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pair;
    }

    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source instanceof String schema) {
            return new AvroSchema(schema);
        }
        if (source instanceof ParsedSchema schema) {
            return schema.canonicalString();
        }
        return null;
    }

}
