package by.sergo.rideservice.domain.dto.response;

import by.sergo.rideservice.domain.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RideResponse {
    private String id;
    private String pickUpAddress;
    private String destinationAddress;
    private Double price;
    private PassengerResponse passengerResponse;
    private DriverResponse driverResponse;
    private LocalDateTime creatingTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
    private String paymentMethod;
}
