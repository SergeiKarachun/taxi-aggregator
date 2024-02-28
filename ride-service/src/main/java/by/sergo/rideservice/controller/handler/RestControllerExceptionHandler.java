package by.sergo.rideservice.controller.handler;

import by.sergo.rideservice.service.exception.BadRequestException;
import by.sergo.rideservice.service.exception.NotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;


@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFoundException(NotFoundException ex) {
        return createResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<RestErrorResponse> handleBadRequestException(BadRequestException ex) {
        return createResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error(errors.toString());
        var response = ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errors(errors)
                .message("Please check input parameters")
                .time(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(value = {FeignException.class})
    public ResponseEntity<RestErrorResponse> handleFeignException(FeignException feignException) {
        var start = feignException.getMessage().indexOf("[\"");
        var end = feignException.getMessage().indexOf("\"]");
        String message = feignException.getMessage().substring(start + 2, end);
        log.error(message);
        return new ResponseEntity<>(RestErrorResponse.builder()
                .messages(Collections.singletonList(message))
                .status(HttpStatus.valueOf(feignException.status()))
                .time(LocalDateTime.now())
                .build(), HttpStatus.valueOf(feignException.status()));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<RestErrorResponse> handleServerException(ServerException ex) {
        return createResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<RestErrorResponse> createResponse(Exception ex, HttpStatus status) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(RestErrorResponse.builder()
                .messages(Collections.singletonList(ex.getMessage()))
                .status(status)
                .time(LocalDateTime.now())
                .build(), status);
    }
}
