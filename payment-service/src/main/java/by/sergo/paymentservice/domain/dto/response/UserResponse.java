package by.sergo.paymentservice.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Double rating;
}
