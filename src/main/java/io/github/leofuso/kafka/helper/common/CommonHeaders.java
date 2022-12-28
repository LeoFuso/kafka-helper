package io.github.leofuso.kafka.helper.common;

import java.nio.charset.*;

import org.springframework.http.*;

@SuppressWarnings("DuplicateStringLiteralInspection")
public final class CommonHeaders {

    /**
     * A String equivalent of {@link CommonHeaders#APPLICATION_AVRO_JSON_UTF8}.
     */
    public static final String APPLICATION_AVRO_JSON_VALUE = "application/vnd.apache.avro+json";

    /**
     * Public constant media type for {@code application/vnd.apache.avro+json;charset=UTF-8}.
     */
    public static final MediaType APPLICATION_AVRO_JSON_UTF8 = new MediaType("application", "vnd.apache.avro+json", StandardCharsets.UTF_8);

    /**
     * A String equivalent of {@link CommonHeaders#APPLICATION_SCHEMA_AVRO_JSON_UTF8}.
     */
    public static final String APPLICATION_SCHEMA_AVRO_JSON_VALUE = "application/vnd.apache.schema-avro+json";

    /**
     * Public constant media type for {@code application/vnd.apache.schema-avro+json;charset=UTF-8}.
     */
    public static final MediaType APPLICATION_SCHEMA_AVRO_JSON_UTF8 = new MediaType(
            "application",
            "vnd.apache.schema-avro+json",
            StandardCharsets.UTF_8
    );

    /**
     * Header name containing the identification of the Schema in the Schema Registry
     */
    public static final String X_SCHEMA_ID = "X-Schema-ID";

    /**
     * Header name containing the Schema version in the Schema Registry
     */
    public static final String X_SCHEMA_VERSION = "X-Schema-Version";

    /**
     * Header name containing the canonical JSON-equivalent of the Schema
     */
    public static final String X_SCHEMA = "X-Schema";

    /**
     * Header name containing the topic destination name
     */
    public static final String X_TOPIC = "X-Topic";

    /**
     * Header name containing the Record key
     */
    public static final String X_KEY = "X-Key";

    /**
     * Header name containing the topic partition destination
     */
    public static final String X_PARTITION = "X-Partition";

    /**
     * Header name containing the timestamp to apply to the produced Record
     */
    public static final String X_TIMESTAMP = "X-Timestamp";

    /**
     * Header name containing the offset of the produced Record
     */
    public static final String X_OFFSET = "X-Offset";

    private CommonHeaders() {
        throw new AssertionError("Suppress default constructor for illegal usage.");
    }
}
