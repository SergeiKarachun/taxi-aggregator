package by.sergo.rideservice.domain.dto.response;

import by.sergo.rideservice.domain.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RideResponseDto {
    String id;
    String pickUpAddress;
    String destinationAddress;
    Double price;
    Long passengerId;
    Long driverId;
    LocalDateTime creatingTime;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Status status;
}
