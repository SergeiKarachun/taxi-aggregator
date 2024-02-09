package by.sergo.paymentservice.domain.entity;

import by.sergo.paymentservice.domain.enums.Operation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transaction_store")
@Entity
@EqualsAndHashCode(exclude = "operationDate")
public class TransactionStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String creditCardNumber;
    private String accountNumber;
    private LocalDateTime operationDate;
    @Enumerated(EnumType.STRING)
    private Operation operation;
    private BigDecimal value;
}
