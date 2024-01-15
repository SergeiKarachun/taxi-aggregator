package by.sergo.passengerservice.repository;

import by.sergo.passengerservice.domain.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query(value = "SELECT AVG(rt.grade) " +
                   "FROM (SELECT grade FROM rating " +
                   "WHERE passenger_id = :passengerId " +
                   "ORDER BY id DESC LIMIT 5) rt",
            nativeQuery = true)
    Optional<Double> getRatingsByPassengerId(Long passengerId);
    Boolean existsByRideId(Long rideId);
    Boolean existsByPassengerId(Long passengerId);
}
