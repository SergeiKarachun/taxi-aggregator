package by.sergo.driverservice.domain.entity;

import by.sergo.driverservice.domain.enums.Status;
import by.sergo.driverservice.util.ConstantUtil;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "car")
@ToString(exclude = "car")
@Entity
@Table(name = "driver")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column(nullable = false, unique = true)
    private String email;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;
    @Builder.Default
    private Double rating = ConstantUtil.DEFAULT_RATING;
    @OneToOne(mappedBy = "driver", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Car car;
}
