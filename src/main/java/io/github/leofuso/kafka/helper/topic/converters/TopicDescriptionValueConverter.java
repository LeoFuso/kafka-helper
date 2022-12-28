package io.github.leofuso.kafka.helper.topic.converters;

import java.util.*;

import org.springframework.core.convert.converter.*;
import org.springframework.stereotype.*;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.*;

@Component
public class TopicDescriptionValueConverter implements Converter<TopicDescription, TopicDescriptionValue> {

    @Override
    public TopicDescriptionValue convert(final TopicDescription source) {

        final List<TopicPartitionInfoValue> partitionInfos = new ArrayList<>(50);
        source.partitions()
                .forEach(partition -> {
                    final int partitionNumber = partition.partition();
                    final Node leader = partition.leader();
                    final TopicPartitionInfoValue infoValue = new TopicPartitionInfoValue(
                            partitionNumber,
                            leader != null ? leader.host() : null,
                            leader != null ? leader.port() : 0
                    );
                    partitionInfos.add(infoValue);
                });
        final Uuid uuid = source.topicId();
        return new TopicDescriptionValue(uuid.toString(), source.name(), partitionInfos);
    }
}
