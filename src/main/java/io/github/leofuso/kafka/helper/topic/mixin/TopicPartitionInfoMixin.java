package io.github.leofuso.kafka.helper.topic.mixin;

import javax.annotation.*;

import java.util.*;

import org.springframework.boot.jackson.*;

import org.apache.kafka.common.*;

import com.fasterxml.jackson.annotation.*;

@SuppressWarnings("unused")
@JsonMixin(TopicPartitionInfo.class)
public abstract class TopicPartitionInfoMixin {

    @JsonProperty("partition")
    private int partition;

    @Nullable
    @JsonProperty("leader")
    private Node leader;

    @JsonProperty("replicas")
    private List<Node> replicas;

    @JsonProperty("isr")
    private List<Node> isr;

}
