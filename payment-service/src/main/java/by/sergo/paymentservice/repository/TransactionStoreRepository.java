package by.sergo.paymentservice.repository;

import by.sergo.paymentservice.domain.entity.TransactionStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionStoreRepository extends JpaRepository<TransactionStore, Long> {
    Page<TransactionStore> findAllByAccountNumber(String accountNumber, Pageable pageable);

    Page<TransactionStore> findAllByCreditCardNumber(String creditCardNumber, Pageable pageable);
}
