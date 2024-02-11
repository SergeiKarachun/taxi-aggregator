package by.sergo.rideservice.controller.handler;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "time")
public class RestErrorResponse {
    private List<String> messages;
    private HttpStatus status;
    private LocalDateTime time;
}
