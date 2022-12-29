package org.apache.kafka.clients.admin;

import java.util.*;

public class TopicDescriptionAndConfig extends TopicDescription {

    private final Map<String, String> configs;

    /**
     * Create an instance with the specified parameters.
     *
     * @param topicDescription The original topic description object
     * @param configs          The configs of this topic
     */
    public TopicDescriptionAndConfig(final TopicDescription topicDescription, final Map<String, String> configs) {
        super(
                topicDescription.name(),
                topicDescription.isInternal(),
                topicDescription.partitions(),
                topicDescription.authorizedOperations(),
                topicDescription.topicId()
        );
        this.configs = Objects.requireNonNull(configs, Map.class.getSimpleName() + " [configs] is required.");
    }
}
