package by.sergo.driverservice.repository;

import by.sergo.driverservice.domain.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /*select avg(dt.vix)
from
(select vix from my_table order by date desc limit 3) dt*/
    @Query(value = "SELECT AVG(rt.grade) " +
                   "FROM (SELECT grade FROM rating " +
                   "ORDER BY id DESC LIMIT 5) rt",
    nativeQuery = true)
    Optional<Double> getRatingsByDriverId(Long driverId);

    Boolean existsByRideId(Long rideId);
}
