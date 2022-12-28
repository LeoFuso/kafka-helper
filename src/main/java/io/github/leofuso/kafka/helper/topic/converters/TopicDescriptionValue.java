package io.github.leofuso.kafka.helper.topic.converters;

import java.util.*;

public record TopicDescriptionValue(String id, String name, List<TopicPartitionInfoValue> partitions) {}
