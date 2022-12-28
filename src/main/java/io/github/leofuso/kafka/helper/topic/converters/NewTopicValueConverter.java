package io.github.leofuso.kafka.helper.topic.converters;

import java.util.*;

import org.springframework.core.convert.converter.*;
import org.springframework.stereotype.*;

import org.apache.kafka.clients.admin.*;

@Component
public class NewTopicValueConverter implements Converter<NewTopicValue, NewTopic> {

    @Override
    public NewTopic convert(final NewTopicValue source) {

        final Map<Integer, List<Integer>> assignments = source.replicasAssignments();

        final NewTopic topic;
        final String name = source.name();
        if (assignments != null && assignments.isEmpty()) {
            topic = new NewTopic(
                    name,
                    Optional.ofNullable(source.numPartitions()),
                    Optional.ofNullable(source.replicationFactor())
            );
        } else {
            topic = new NewTopic(
                    name,
                    assignments != null ? assignments : Map.of()
            );
        }

        final Map<String, String> configs = source.configs();
        if (configs != null) {
            topic.configs(configs);
        }
        return topic;
    }
}
