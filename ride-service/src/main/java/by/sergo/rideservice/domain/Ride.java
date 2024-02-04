package by.sergo.rideservice.domain;

import by.sergo.rideservice.domain.enums.PaymentMethod;
import by.sergo.rideservice.domain.enums.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.bson.types.ObjectId;
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
    ObjectId id;
    @NotNull
    @Size(max = 255)
    String pickUpAddress;
    @NotNull
    @Size(max = 255)
    String destinationAddress;
    Double price;
    Long driverId;
    @NotNull
    Long passengerId;
    @Builder.Default
    LocalDateTime creatingTime = LocalDateTime.now();
    LocalDateTime startTime;
    LocalDateTime endTime;
    Status status = Status.CREATED;
    PaymentMethod paymentMethod;
}
