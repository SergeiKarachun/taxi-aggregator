package by.sergo.paymentservice.repository;

import by.sergo.paymentservice.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByDriverId(Long driverId);
    Optional<Account> findByAccountNumber(String accountNumber);

    Boolean existsByAccountNumber(String accountNumber);

    Boolean existsByDriverId(Long accountNumber);
}
