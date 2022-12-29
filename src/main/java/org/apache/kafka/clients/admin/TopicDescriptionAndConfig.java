package org.apache.kafka.clients.admin;

import javax.annotation.*;

import java.util.*;

import org.apache.kafka.common.*;

/**
 * A detailed description with config values of a single topic in the cluster.
 */
public class TopicDescriptionAndConfig extends TopicDescription {

    /**
     * All {@link org.apache.kafka.common.config.Config Config} entries in a name:valeu format.
     */
    private final Map<String, String> configs;

    /**
     * Create an instance with the specified parameters.
     *
     * @param description The original topic description object
     * @param configs     The configs of this topic
     */
    public TopicDescriptionAndConfig(TopicDescription description, @Nullable Map<String, String> configs) {
        super(
                /* @formatter:off */
                description.name(),
                description.isInternal(),
                Optional.ofNullable(description.partitions()).orElseGet(List::of),
                Optional.ofNullable(description.authorizedOperations()).orElseGet(Set::of),
                Optional.ofNullable(description.topicId()).orElse(Uuid.ZERO_UUID)
                /* @formatter:on */
        );
        this.configs = Optional.ofNullable(configs)
                .map(HashMap::new)
                .orElseGet(HashMap::new);
    }

    /**
     * @return a copy of all configurations associated with this topic.
     */
    public Map<String, String> getConfigs() {
        return new HashMap<>(configs);
    }
}
