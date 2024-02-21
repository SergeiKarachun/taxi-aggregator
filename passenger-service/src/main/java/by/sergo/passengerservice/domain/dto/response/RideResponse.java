package by.sergo.passengerservice.domain.dto.response;

import by.sergo.passengerservice.domain.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse {
    private String id;
    private String pickUpAddress;
    private String destinationAddress;
    private Double price;
    private Long passengerId;
    private Long driverId;
    private LocalDateTime creatingTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
    private String paymentMethod;
}
