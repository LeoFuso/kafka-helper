package io.github.leofuso.kafka.helper.topic.mixin;

import org.springframework.boot.jackson.*;

import org.apache.kafka.common.*;

import com.fasterxml.jackson.annotation.*;

@JsonMixin(Node.class)
@SuppressWarnings("unused")
@JsonIgnoreProperties(value = { "id", "rack", "hash" })
public class NodeMixin {

    @JsonProperty("id")
    private String idString;

    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private int port;

}
