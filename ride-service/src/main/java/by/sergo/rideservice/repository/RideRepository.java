package by.sergo.rideservice.repository;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {
    List<Ride> findByStatus(Status status);

    Page<Ride> findAllByPassengerIdAndStatus(long passengerId, Status status, PageRequest pageRequest);

    Page<Ride> findAllByDriverIdAndStatus(long driverId, Status status, PageRequest pageRequest);
}
