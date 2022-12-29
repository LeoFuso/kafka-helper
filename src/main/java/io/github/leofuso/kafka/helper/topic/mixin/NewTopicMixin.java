package io.github.leofuso.kafka.helper.topic.mixin;

import java.util.*;

import org.springframework.boot.jackson.*;

import org.apache.kafka.clients.admin.*;

import com.fasterxml.jackson.annotation.*;

@JsonMixin(NewTopic.class)
@SuppressWarnings("unused")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
abstract class NewTopicMixin {


    /**
     * Weird things happening here.
     * <p>
     * Without this constructor, Jackson cannot instantiate the {@link NewTopic} class, even if it does not use this constructor.
     * <p>
     * At least one constructor must be declared, and it cannot be an empty constructor, e.g.
     * <pre>
     *     <code>
     *         NewTopicMixin() {}
     *     </code>
     * </pre>
     * <p>
     * That said, this declaration <b>alone</b> is enough to enable the creation of {@link NewTopic} instances using both
     * {@link NewTopic#NewTopic(String, int, short)} and {@link NewTopic#NewTopic(String, Map)}.
     * <p>
     * Althought, without declaring...
     * <pre>
     *     <code>
     *         {@literal @}JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
     *     </code>
     * </pre>
     * ... the {@link NewTopic#replicasAssignments} field is not populated.
     * <p>
     * There's some unpredictable behavior happening here.
     */
    NewTopicMixin(
            @JsonProperty("name") String name,
            @JsonProperty(value = "numPartitions", defaultValue = "-1") int numPartitions,
            @JsonProperty(value = "replicationFactor", defaultValue = "-1") short replicationFactor
    ) {}

    @JsonSetter("configs")
    abstract NewTopic configs(Map<String, String> configs);
}
