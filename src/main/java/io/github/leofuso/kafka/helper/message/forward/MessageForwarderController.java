package io.github.leofuso.kafka.helper.message.forward;

import java.util.*;

import org.springframework.http.*;
import org.springframework.messaging.*;
import org.springframework.web.bind.annotation.*;

import org.apache.avro.generic.*;

import static io.github.leofuso.kafka.helper.common.HeaderConstants.*;

@RestController
@RequestMapping("forward")
public class MessageForwarderController {

    private final MessageForwarderService service;

    public MessageForwarderController(final MessageForwarderService service) {
        this.service = Objects.requireNonNull(service, MessageForwarderService.class.getSimpleName() + " [service] is required.");
    }

    @PostMapping(headers = { X_SCHEMA_ID, X_SCHEMA }, consumes = APPLICATION_AVRO_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Message<GenericData.Record> forward(final Message<GenericData.Record> message) {
        return service.forward(message);
    }

}
