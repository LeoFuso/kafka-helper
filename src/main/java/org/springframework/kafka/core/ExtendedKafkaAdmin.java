package org.springframework.kafka.core;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.*;
import java.util.stream.*;

import org.springframework.kafka.*;

import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.*;
import org.apache.kafka.common.errors.*;
import org.slf4j.*;

import static org.apache.kafka.common.config.ConfigResource.Type.*;

public class ExtendedKafkaAdmin extends KafkaAdmin implements ExtendedKafkaAdminOperations {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedKafkaAdmin.class);

    private static final int DEFAULT_OPERATION_TIMEOUT = 30;

    private int operationTimeout = DEFAULT_OPERATION_TIMEOUT;

    /**
     * Create an instance with an {@link AdminClient} based on the supplied configuration.
     *
     * @param config the configuration for the {@link AdminClient}.
     */
    public ExtendedKafkaAdmin(Map<String, Object> config) {
        super(config);
    }

    private static Map<String, Map<String, String>> flapMapConfigs(Stream<Map.Entry<ConfigResource, Config>> stream) {
        return stream.map(entry -> {

                    final ConfigResource key = entry.getKey();
                    final Config value = entry.getValue();

                    final Map<String, String> topicConfig = value.entries()
                            .stream()
                            .collect(Collectors.toMap(ConfigEntry::name, ConfigEntry::value));

                    return Map.entry(key.name(), topicConfig);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteTopics(String... topics) {
        try (AdminClient client = createAdmin()) {

            Arrays.stream(topics)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), client::deleteTopics))
                    .all()
                    .get(operationTimeout, TimeUnit.SECONDS);

        } catch (ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof UnknownTopicOrPartitionException ex) {
                final String message = ex.getMessage();
                logger.warn(message, ex);
                return;
            }
            throw new KafkaException("Failed to remove topics: " + Arrays.toString(topics), e);
        } catch (InterruptedException ie) {
            final Thread currentThread = Thread.currentThread();
            currentThread.interrupt();
            throw new KafkaException("Interrupted while removing topics: " + Arrays.toString(topics), ie);
        } catch (TimeoutException e) {
            throw new KafkaException("Timeout while removing topics: " + Arrays.toString(topics), e);
        }
    }

    @Override
    public Map<String, TopicDescriptionAndConfig> describeTopicsWithConfigs(String... topics) {
        try (AdminClient client = createAdmin()) {

            return doDescribeTopics(client, topics)
                    .thenCombine(
                            describeTopicConfigs(client, topics),
                            (c1, c2) -> {

                                final Map<String, TopicDescriptionAndConfig> tdcMap = new HashMap<>(c1.size());
                                c1.forEach((key, td) -> {
                                    final Map<String, String> configs = c2.get(key);
                                    final TopicDescriptionAndConfig tdc = new TopicDescriptionAndConfig(td, configs);
                                    tdcMap.put(key, tdc);
                                });
                                return tdcMap;

                            })
                    .toCompletableFuture()
                    .get(operationTimeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof UnknownTopicOrPartitionException ex) {
                final String message = ex.getMessage();
                logger.warn(message, ex);
                return Map.of();
            }
            throw new KafkaException("Failed to retrieve topic descriptions and configs: " + Arrays.toString(topics), e);
        } catch (InterruptedException ie) {
            final Thread currentThread = Thread.currentThread();
            currentThread.interrupt();
            throw new KafkaException("Interrupted while retrieving topic descriptions and configs: " + Arrays.toString(topics), ie);
        } catch (TimeoutException e) {
            throw new KafkaException("Timeout while retrieving topic descriptions and configs: " + Arrays.toString(topics), e);
        }
    }

    @Override
    public AdminClient createAdmin() {
        final HashMap<String, Object> config = new HashMap<>(getConfigurationProperties());
        checkBootstrap(config);
        return AdminClient.create(config);
    }

    @Override
    public void setOperationTimeout(final int operationTimeout) {
        super.setOperationTimeout(operationTimeout);
        this.operationTimeout = operationTimeout;
    }

    private CompletionStage<Map<String, TopicDescription>> doDescribeTopics(AdminClient client, String... topicNames) {
        return Arrays.stream(topicNames)
                .collect(Collectors.collectingAndThen(Collectors.toList(), client::describeTopics))
                .allTopicNames()
                .toCompletionStage();
    }

    public CompletionStage<Map<String, Map<String, String>>> describeTopicConfigs(AdminClient client, String... topicNames) {
        return Arrays.stream(topicNames)
                .map(name -> new ConfigResource(TOPIC, name))
                .collect(Collectors.collectingAndThen(Collectors.toList(), client::describeConfigs))
                .all()
                .toCompletionStage()
                .thenApply(Map::entrySet)
                .thenApply(Set::stream)
                .thenApply(ExtendedKafkaAdmin::flapMapConfigs);
    }
}
