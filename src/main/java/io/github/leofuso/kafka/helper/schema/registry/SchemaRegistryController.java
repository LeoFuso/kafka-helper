package io.github.leofuso.kafka.helper.schema.registry;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.*;

import org.apache.avro.*;

import static io.github.leofuso.kafka.helper.common.CommonHeaders.*;

@RestController
@RequestMapping("schema-registry")
public class SchemaRegistryController {

    private final SchemaRegistryService service;

    public SchemaRegistryController(final SchemaRegistryService registryService) {
        service = registryService;
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_SCHEMA_AVRO_JSON_VALUE)
    public Schema schemaById(@PathVariable int id) {
        return service.accessById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            path = "/{subject}",
            consumes = APPLICATION_SCHEMA_AVRO_JSON_VALUE,
            produces = APPLICATION_SCHEMA_AVRO_JSON_VALUE
    )
    public ResponseEntity<Schema> register(@PathVariable String subject, @RequestBody Schema schema) {
        final Integer id = service.register(subject, schema);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(id)
                                .toUri()
                )
                .header(X_SCHEMA_ID, String.valueOf(id))
                .header(X_SCHEMA_VERSION, "1")
                .body(schema);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(
            path = "/{subject}/{version}",
            consumes = APPLICATION_SCHEMA_AVRO_JSON_VALUE,
            produces = APPLICATION_SCHEMA_AVRO_JSON_VALUE
    )
    public ResponseEntity<Schema> register(@PathVariable String subject, @PathVariable int version, @RequestBody Schema schema) {
        final Integer id = service.register(subject, schema, version);
        return ResponseEntity.status(HttpStatus.OK)
                .header(X_SCHEMA_ID, String.valueOf(id))
                .header(X_SCHEMA_VERSION, String.valueOf(version))
                .body(schema);
    }
}
