package io.github.leofuso.kafka.helper.common;

import org.springframework.http.*;

public final class HeaderConstants {

    /**
     * A String equivalent of {@link HeaderConstants#APPLICATION_AVRO}.
     */
    public static final String APPLICATION_AVRO_VALUE = "application/avro";

    /**
     * Public constant media type for {@code application/avro}.
     */
    @SuppressWarnings("WeakerAccess")
    public static final MediaType APPLICATION_AVRO = MediaType.parseMediaType(APPLICATION_AVRO_VALUE);

    /**
     * Header name containing the identification of the Schema in the Schema Registry
     */
    public static final String X_SCHEMA_ID = "X-Schema-ID";

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

    private HeaderConstants() {
        throw new AssertionError("Suppress default constructor for illegal usage.");
    }
}
