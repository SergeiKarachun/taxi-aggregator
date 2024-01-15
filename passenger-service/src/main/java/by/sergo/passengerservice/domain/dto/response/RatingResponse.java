package by.sergo.passengerservice.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingResponse {
    private Long id;
    private Integer grade;
    private Long passengerId;
    private Long driverId;
}
