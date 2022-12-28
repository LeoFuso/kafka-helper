package io.github.leofuso.kafka.helper.topic;

import javax.annotation.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.springframework.core.convert.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.*;

import org.apache.kafka.clients.admin.*;

import io.github.leofuso.kafka.helper.topic.converters.*;

import jakarta.validation.*;


@RestController
@RequestMapping("topics")
@SuppressWarnings("DuplicateStringLiteralInspection")
public class TopicController {

    private final TopicService service;
    private final ConversionService converter;

    public TopicController(final TopicService service, final ConversionService conversionService) {
        this.service = Objects.requireNonNull(service, TopicService.class.getSimpleName() + " [service] is required.");
        converter = Objects.requireNonNull(
                conversionService,
                ConversionService.class.getSimpleName() + " [conversionService] is required."
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, TopicDescriptionValue> list(@RequestParam(name = "q") String query) {
        return service.find(query)
                .values()
                .stream()
                .map(topicDescription -> {
                    final TopicDescriptionValue value = converter.convert(topicDescription, TopicDescriptionValue.class);
                    return Objects.requireNonNull(value);
                })
                .collect(Collectors.toMap(TopicDescriptionValue::name, Function.identity()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TopicDescriptionValue> create(@Valid @RequestBody @Nonnull NewTopicValue value) {

        final NewTopic newTopic = converter.convert(value, NewTopic.class);
        final TopicDescription description = service.create(Objects.requireNonNull(newTopic));
        final TopicDescriptionValue descriptionValue = converter.convert(description, TopicDescriptionValue.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/?q={q}")
                                .buildAndExpand(description.name())
                                .toUri()
                )
                .body(descriptionValue);
    }

}
