package by.sergo.rideservice.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PassengerResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Double rating;
}
