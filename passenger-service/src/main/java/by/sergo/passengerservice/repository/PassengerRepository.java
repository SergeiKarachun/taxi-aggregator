package by.sergo.passengerservice.repository;

import by.sergo.passengerservice.domain.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Optional<Passenger> findByPhone(String phone);
}
