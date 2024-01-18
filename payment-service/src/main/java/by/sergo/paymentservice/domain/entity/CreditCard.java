package by.sergo.paymentservice.domain.entity;

import by.sergo.paymentservice.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "credit_card")
@EqualsAndHashCode(exclude = {"userId", "userType"})
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String creditCardNumber;
    private String cvv;
    private LocalDate expDate;
    private Long userId;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private UserType userType;
}
