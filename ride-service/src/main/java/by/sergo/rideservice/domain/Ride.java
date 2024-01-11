package by.sergo.rideservice.domain;

import by.sergo.rideservice.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Ride {
    @Id
    String id;
    String pickUpAddress;
    String destinationAddress;
    Double price;
    Long passengerId;
    Long driverId;
    LocalDateTime creatingTime;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Status status = Status.CREATED;

}
