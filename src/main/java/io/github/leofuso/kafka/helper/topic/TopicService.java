package io.github.leofuso.kafka.helper.topic;

import java.util.*;
import java.util.concurrent.*;

import org.springframework.kafka.*;
import org.springframework.kafka.core.*;
import org.springframework.stereotype.*;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.errors.*;

@Service
public class TopicService {

    private final KafkaAdminOperations operations;

    public TopicService(final KafkaAdminOperations operations) {
        this.operations = Objects.requireNonNull(operations, KafkaAdminOperations.class.getSimpleName() + " [operations] is required.");
    }

    TopicDescription create(NewTopic topic) {
        operations.createOrModifyTopics(topic);
        final String name = topic.name();
        final Map<String, TopicDescription> descriptionMap = find(name);
        return descriptionMap.get(name);
    }

    Map<String, TopicDescription> find(String query) {
        try {
            return operations.describeTopics(query);
        } catch (KafkaException e) {
            if (e.getCause() instanceof ExecutionException exec && exec.getCause() instanceof UnknownTopicOrPartitionException) {
                return Map.of();
            } else {
                throw e;
            }
        }
    }
}
