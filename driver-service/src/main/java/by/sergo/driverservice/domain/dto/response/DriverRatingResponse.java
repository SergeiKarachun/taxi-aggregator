package by.sergo.driverservice.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DriverRatingResponse {
    private Long driverId;
    private Double rating;
}
