package io.github.leofuso.kafka.helper.topic;

import java.util.*;

import org.apache.kafka.clients.admin.*;

public interface TopicOperations {

    TopicDescriptionAndConfig createOrUpdate(NewTopic topic);

    Map<String, TopicDescriptionAndConfig> describe(String... topics);

    void delete(String... topics);

}
