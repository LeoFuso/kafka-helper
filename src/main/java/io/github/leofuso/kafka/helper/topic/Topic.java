package io.github.leofuso.kafka.helper.topic;

import java.util.*;

import org.springframework.kafka.core.*;
import org.springframework.stereotype.*;

import org.apache.kafka.clients.admin.*;

@Service
public class Topic implements TopicOperations {

    private final ExtendedKafkaAdminOperations delegate;

    public Topic(final ExtendedKafkaAdminOperations delegate) {
        this.delegate = Objects.requireNonNull(
                delegate,
                ExtendedKafkaAdminOperations.class.getSimpleName() + " [delegate] is required."
        );
    }

    @Override
    public TopicDescriptionAndConfig createOrUpdate(final NewTopic topic) {
        delegate.createOrModifyTopics(topic);
        final String name = topic.name();
        final Map<String, TopicDescriptionAndConfig> descriptionMap = describe(name);
        return descriptionMap.get(name);
    }

    @Override
    public Map<String, TopicDescriptionAndConfig> describe(final String... topics) {
        return delegate.describeTopicsWithConfigs(topics);
    }

    @Override
    public void delete(final String... topics) {
        delegate.deleteTopics(topics);
    }
}
