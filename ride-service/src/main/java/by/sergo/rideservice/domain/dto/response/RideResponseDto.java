package by.sergo.rideservice.domain.dto.response;

import by.sergo.rideservice.domain.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    Long driverId;
    LocalDateTime creatingTime;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Status status;
}
