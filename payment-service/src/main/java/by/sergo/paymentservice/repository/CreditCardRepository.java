package by.sergo.paymentservice.repository;

import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByUserIdAndUserType(Long userId, UserType userType);

    Boolean existsByCreditCardNumber(String creditCardNumber);
}
