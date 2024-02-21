package by.sergo.passengerservice.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RatingResponse {
    private Long id;
    private Integer grade;
    private Long passengerId;
    private DriverResponse driver;
    private RideResponse ride;
}
