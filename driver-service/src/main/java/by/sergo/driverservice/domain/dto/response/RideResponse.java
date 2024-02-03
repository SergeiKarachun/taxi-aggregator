package by.sergo.driverservice.domain.dto.response;

import by.sergo.driverservice.domain.enums.RideStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
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
    private RideStatus status;
    private String paymentMethod;
}
