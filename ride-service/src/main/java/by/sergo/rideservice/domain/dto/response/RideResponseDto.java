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
    private String id;
    private String pickUpAddress;
    private String destinationAddress;
    private Double price;
    private Long passengerId;
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    private Long driverId;
    private LocalDateTime creatingTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
}
