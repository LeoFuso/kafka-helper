package io.github.leofuso.kafka.helper.topic;

import java.util.*;

import org.springframework.kafka.core.*;
import org.springframework.stereotype.*;

import org.apache.kafka.clients.admin.*;

@Service
public class TopicService {

    private final ExtendedKafkaAdminOperations operations;

    public TopicService(final ExtendedKafkaAdminOperations operations) {
        this.operations = Objects.requireNonNull(
                operations,
                ExtendedKafkaAdminOperations.class.getSimpleName() + " [operations] is required."
        );
    }

    TopicDescription create(NewTopic topic) {
        operations.createOrModifyTopics(topic);
        final String name = topic.name();
        final Map<String, TopicDescription> descriptionMap = find(name);
        return descriptionMap.get(name);
    }

    Map<String, TopicDescription> find(String query) {
        return operations.describeTopics(query);
    }
}
