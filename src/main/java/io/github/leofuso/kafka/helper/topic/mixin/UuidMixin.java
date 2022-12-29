package io.github.leofuso.kafka.helper.topic.mixin;

import org.springframework.boot.jackson.*;

import org.apache.kafka.common.*;

import com.fasterxml.jackson.annotation.*;
@SuppressWarnings("unused")
@JsonMixin(Uuid.class)
public abstract class UuidMixin {

    @Override
    @JsonValue
    public abstract String toString();

}
