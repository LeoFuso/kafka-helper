package io.github.leofuso.kafka.helper.schema.registry;

import io.confluent.kafka.schemaregistry.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schema-registry")
public class SchemaRegistryController {

    private final SchemaRegistryService service;

    public SchemaRegistryController(SchemaRegistryService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public String schemaById(@PathVariable int id) throws Exception {
        return service.accessById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{subject}", consumes = "application/json")
    public int register(@PathVariable String subject, @RequestBody ParsedSchema schema) throws Exception {
        return service.register(subject, schema);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{subject}/{version}", consumes = "application/json")
    public int registerVersion(@PathVariable String subject, @PathVariable int version, @RequestBody ParsedSchema schema) throws Exception {
        return service.register(subject, schema, version);
    }
}
