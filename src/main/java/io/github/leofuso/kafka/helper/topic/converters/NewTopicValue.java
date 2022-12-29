package io.github.leofuso.kafka.helper.topic.converters;

import javax.annotation.*;

import java.util.*;

import org.springframework.validation.annotation.*;

@Validated
public record NewTopicValue(
        String name,
        @Nullable Integer numPartitions,
        @Nullable Short replicationFactor,
        @Nullable Map<Integer, List<Integer>> replicasAssignments,
        @Nullable Map<String, String> configs
) {

    public NewTopicValue {

        replicasAssignments = Optional.ofNullable(replicasAssignments)
                .orElseGet(Map::of);

        configs = Optional.ofNullable(configs)
                .orElseGet(Map::of);
    }
}
