package by.sergo.rideservice.controller.handler;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "time")
public class ValidationExceptionResponse {
    private HttpStatus status;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime time;
}
