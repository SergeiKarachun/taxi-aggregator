package by.sergo.passengerservice.service.exception;

import org.springframework.http.HttpStatus;

import java.util.Date;

public record ErrorMessageResponse(String message,
                                   HttpStatus status,
                                   Date date) {
}
