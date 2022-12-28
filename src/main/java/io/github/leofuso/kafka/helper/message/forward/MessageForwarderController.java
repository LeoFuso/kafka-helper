package io.github.leofuso.kafka.helper.message.forward;

import java.util.*;

import org.springframework.http.*;
import org.springframework.messaging.*;
import org.springframework.web.bind.annotation.*;

import org.apache.avro.generic.*;

import static io.github.leofuso.kafka.helper.common.CommonHeaders.*;

@RestController
@RequestMapping("forward")
public class MessageForwarderController {

    private final MessageForwarderService service;

    MessageForwarderController(final MessageForwarderService service) {
        this.service = Objects.requireNonNull(service, MessageForwarderService.class.getSimpleName() + " [service] is required.");
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(consumes = APPLICATION_AVRO_JSON_VALUE, produces = APPLICATION_AVRO_JSON_VALUE)
    public Message<GenericData.Record> forward(@RequestBody final Message<GenericData.Record> message) {
        return service.forward(message);
    }

}
