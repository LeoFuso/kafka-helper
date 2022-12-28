package io.github.leofuso.kafka.helper.topic.converters;

import javax.annotation.*;

record TopicPartitionInfoValue(int partition, @Nullable String host, int port) {}
