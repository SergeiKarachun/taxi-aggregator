package by.sergo.driverservice.domain.entity;

import by.sergo.driverservice.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static by.sergo.driverservice.util.ConstantUtil.DEFAULT_BALANCE;
import static by.sergo.driverservice.util.ConstantUtil.DEFAULT_RATING;

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
    private Double rating = DEFAULT_RATING;
    @Builder.Default
    private BigDecimal balance = DEFAULT_BALANCE;
    @OneToOne(mappedBy = "driver", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Car car;
}
