package by.sergo.driverservice.controller.handler;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "time")
public class ValidationExceptionResponse {
    private HttpStatus status;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime time;
}
