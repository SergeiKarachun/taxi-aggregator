package by.sergo.driverservice.repository;

import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.domain.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    Page<Driver> getAllByStatus(Status status, PageRequest pageRequest);
}
