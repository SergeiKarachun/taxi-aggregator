package by.sergo.paymentservice.controller.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationExceptionResponse {
    private HttpStatus status;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime time;
}
