package by.sergo.driverservice.repository;

import by.sergo.driverservice.domain.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> getCarByDriverId(Long driverId);
    boolean existsByNumber(String number);
    boolean existsByDriverId(Long driverId);
}
