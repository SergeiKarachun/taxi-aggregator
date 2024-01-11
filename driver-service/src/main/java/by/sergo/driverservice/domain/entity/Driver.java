package by.sergo.driverservice.domain.entity;

import by.sergo.driverservice.domain.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;
    @Builder.Default
    private Double rating = 5.0;
    @OneToOne(mappedBy = "driver", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Car car;
}
