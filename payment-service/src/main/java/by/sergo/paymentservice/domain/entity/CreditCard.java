package by.sergo.paymentservice.domain.entity;

import by.sergo.paymentservice.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "credit_card")
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
