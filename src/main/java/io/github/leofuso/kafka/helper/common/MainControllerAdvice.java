package io.github.leofuso.kafka.helper.common;

import java.net.*;

import org.springframework.http.*;
import org.springframework.web.*;
import org.springframework.web.bind.annotation.*;

import io.confluent.kafka.schemaregistry.client.rest.exceptions.*;


@ControllerAdvice(annotations = RestController.class)
public class MainControllerAdvice {

    @ExceptionHandler(RestClientException.class)
    public ErrorResponse handle(RestClientException ex) {
        final int code = ex.getStatus();
        final String message = ex.getMessage();
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(code), message);
    }

    @ExceptionHandler(ConnectException.class)
    public ErrorResponse handle(ConnectException ex) {
        final String message = ex.getMessage();
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
