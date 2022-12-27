package io.github.leofuso.kafka.helper.schema.registry;

import java.io.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import io.confluent.kafka.schemaregistry.*;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.*;

@RestController
@RequestMapping("schema-registry")
public class SchemaRegistryController {

    private final SchemaRegistryService service;

    public SchemaRegistryController(final SchemaRegistryService registryService) {
        service = registryService;
    }

    @GetMapping("/{id}")
    public ParsedSchema schemaById(@PathVariable int id) throws RestClientException, IOException {
        return service.accessById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{subject}")
    public int register(@PathVariable String subject, @RequestBody ParsedSchema schema) throws RestClientException, IOException {
        return service.register(subject, schema);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{subject}/{version}", consumes = "text/plain")
    public int register(@PathVariable String subject, @PathVariable int version, @RequestBody ParsedSchema schema) throws RestClientException, IOException {
        return service.register(subject, schema, version);
    }
}
