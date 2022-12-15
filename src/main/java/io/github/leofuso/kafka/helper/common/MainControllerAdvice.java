package io.github.leofuso.kafka.helper.common;

import io.confluent.kafka.schemaregistry.client.rest.exceptions.*;

import org.springframework.http.*;
import org.springframework.web.*;
import org.springframework.web.bind.annotation.*;


@ControllerAdvice(annotations = RestController.class)
public class MainControllerAdvice {

    @ExceptionHandler({ RestClientException.class })
    public ErrorResponse handle(RestClientException ex) {
        final int code = ex.getStatus();
        final String message = ex.getMessage();
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(code), message);
    }

}
