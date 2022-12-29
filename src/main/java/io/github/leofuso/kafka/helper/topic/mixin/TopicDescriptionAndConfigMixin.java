package io.github.leofuso.kafka.helper.topic.mixin;

import java.util.*;

import org.springframework.boot.jackson.*;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.*;
import org.apache.kafka.common.acl.*;

import com.fasterxml.jackson.annotation.*;

@SuppressWarnings("unused")
@JsonMixin(TopicDescriptionAndConfig.class)
public abstract class TopicDescriptionAndConfigMixin {

    @JsonProperty("id")
    private Uuid topicId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("internal")
    private boolean internal;

    @JsonProperty("partitions")
    private List<TopicPartitionInfo> partitions;

    @JsonProperty("configs")
    private Map<String, String> configs;

    @JsonIgnore
    private Set<AclOperation> authorizedOperations;

}
