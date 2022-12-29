package org.springframework.kafka.core;

import java.util.*;

import org.apache.kafka.clients.admin.*;

/**
 * An extesion of the {@link KafkaAdminOperations} adding more utilities.
 */
public interface ExtendedKafkaAdminOperations extends KafkaAdminOperations {

    /**
     * Remove a batch of topics.
     * <p>
     * This operation is not transactional, so it may succeed for some topics while fail for others.
     * <p>
     * It may take several seconds to the invokation returns success for all the brokers to become aware the topics are gone. During this
     * time, {@link #describeTopics(String...)}, {@link #describeTopicsWithConfigs(String...)} may continue to return information about the
     * deleted topics.
     * <p>
     * If delete.topic.enable is false on the brokers, deleteTopics marks the topics for deletion, but not actually delete them.
     * <p>
     * When using topic IDs, this operation is supported by brokers with inter-broker protocol 2.8 or higher. When using topic names, this
     * operation is supported by brokers with version 0.10.1.0 or higher.
     *
     * @param topics The topics to delete.
     */
    void deleteTopics(String... topics);

    /**
     * Retrieve {@link TopicDescription}s with {@code Configuration}s for these topics.
     *
     * @param topics the topic names.
     * @return a map of name:topicDescriptionAndConfig.
     */
    Map<String, TopicDescriptionAndConfig> describeTopicsWithConfigs(String... topics);

    /**
     * @return a newly created admin
     */
    AdminClient createAdmin();

}
