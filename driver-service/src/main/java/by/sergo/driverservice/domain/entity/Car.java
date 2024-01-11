package by.sergo.driverservice.domain.entity;

import by.sergo.driverservice.domain.enums.Color;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "driver")
@ToString(exclude = "driver")
@Entity
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private Integer yearOfManufacture;
    @Column(nullable = false, unique = true)
    private String number;
    @Enumerated(EnumType.STRING)
    private Color color;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
