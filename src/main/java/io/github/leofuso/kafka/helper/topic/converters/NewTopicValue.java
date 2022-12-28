package io.github.leofuso.kafka.helper.topic.converters;

import javax.annotation.*;

import java.util.*;

import org.springframework.validation.annotation.*;

import jakarta.validation.constraints.*;

@Validated
public record NewTopicValue(
        String name,
        @Nullable Integer numPartitions,
        @Nullable Short replicationFactor,
        @Nullable Map<Integer, List<Integer>> replicasAssignments,
        @Nullable Map<String, String> configs
) {

    public NewTopicValue(@NotBlank String name,
                         @Nullable Integer numPartitions,
                         @Nullable Short replicationFactor,
                         @Nullable Map<Integer, List<Integer>> replicasAssignments,
                         @Nullable Map<String, String> configs) {

        this.name = name;
        this.numPartitions = numPartitions;
        this.replicationFactor = replicationFactor;

        this.replicasAssignments =
                Optional.ofNullable(replicasAssignments)
                        .orElseGet(Map::of);

        this.configs =
                Optional.ofNullable(configs)
                        .orElseGet(Map::of);
    }
}
