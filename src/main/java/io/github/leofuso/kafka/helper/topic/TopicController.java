package io.github.leofuso.kafka.helper.topic;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.*;

import org.apache.kafka.clients.admin.*;

import jakarta.validation.*;


@RestController
@RequestMapping("topics")
public class TopicController {

    private final TopicOperations operations;

    public TopicController(final TopicOperations operations) {
        this.operations = Objects.requireNonNull(operations, TopicOperations.class.getSimpleName() + " [operations] is required.");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, TopicDescriptionAndConfig> list(@RequestParam(name = "topics") String[] topics) {
        return operations.describe(topics);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TopicDescriptionAndConfig> create(@Valid @RequestBody NewTopic value) {
        final TopicDescriptionAndConfig description = operations.createOrUpdate(value);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/?topics={q}")
                                .buildAndExpand(description.name())
                                .toUri()
                )
                .body(description);
    }
}
