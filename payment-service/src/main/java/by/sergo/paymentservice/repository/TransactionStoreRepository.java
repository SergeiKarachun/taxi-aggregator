package by.sergo.paymentservice.repository;

import by.sergo.paymentservice.domain.entity.TransactionStore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransactionStoreRepository extends JpaRepository<TransactionStore, Long> {
    List<TransactionStore> findAllByAccountNumber(String accountNumber, Pageable pageable);
    List<TransactionStore> findAllByCreditCardNumber(String creditCardNumber, Pageable pageable);
}
