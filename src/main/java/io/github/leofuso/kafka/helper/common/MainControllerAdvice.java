package io.github.leofuso.kafka.helper.common;

import java.lang.reflect.*;
import java.net.*;

import org.springframework.http.*;
import org.springframework.web.*;
import org.springframework.web.bind.annotation.*;


@ControllerAdvice(annotations = RestController.class)
public class MainControllerAdvice {

    @ExceptionHandler(UndeclaredThrowableException.class)
    public ErrorResponse handle(final UndeclaredThrowableException ex) {
        final String message = ex.getMessage();
        final Throwable undeclaredThrowable = ex.getUndeclaredThrowable();
        return ErrorResponse.create(undeclaredThrowable, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(ConnectException.class)
    public ErrorResponse handle(final ConnectException ex) {
        final String message = ex.getMessage();
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ErrorResponse handle(final ErrorResponseException ex) {
        return ex;
    }

}
