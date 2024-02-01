package by.sergo.passengerservice.service;

import by.sergo.passengerservice.client.DriverFeignClient;
import by.sergo.passengerservice.client.RideFeignClient;
import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.*;
import by.sergo.passengerservice.domain.entity.Rating;
import by.sergo.passengerservice.mapper.RatingMapper;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.repository.RatingRepository;
import by.sergo.passengerservice.service.exception.NotFoundException;
import by.sergo.passengerservice.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static by.sergo.passengerservice.util.PassengerTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {

    @Mock
    PassengerRepository passengerRepository;
    @Mock
    RatingRepository ratingRepository;
    @Mock
    RatingMapper ratingMapper;
    @Mock
    DriverFeignClient driverFeignClient;
    @Mock
    RideFeignClient rideFeignClient;

    @InjectMocks
    RatingServiceImpl ratingService;

    @Test
    void rateExistingPassenger() {
        RatingCreateRequest request = getRatingRequest();
        Rating ratingToSave = getDefaulRating();
        Rating savedRating = getDefaulRating();
        RatingResponse response = getDefaultRatingResponse();
        DriverResponse driverResponse = getDefaultDriverResponse();
        RideResponse rideResponse = getDefaultRideResponse();

        doReturn(Optional.of(getDefaultPassenger()))
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        doReturn(ratingToSave)
                .when(ratingMapper)
                .mapToEntity(request);
        doReturn(savedRating)
                .when(ratingRepository)
                .save(ratingToSave);
        doReturn(response)
                .when(ratingMapper)
                .mapToDto(savedRating);
        doReturn(driverResponse)
                .when(driverFeignClient)
                .getDriverById(request.getDriverId());
        doReturn(rideResponse)
                .when(rideFeignClient)
                .getRideById(request.getRideId());
        RatingResponse expected = ratingService.ratePassenger(request, DEFAULT_ID);

        assertNotNull(expected);
        verify(passengerRepository).findById(DEFAULT_ID);
        verify(ratingRepository).save(ratingToSave);
        verify(ratingMapper).mapToDto(savedRating);
        verify(driverFeignClient).getDriverById(request.getDriverId());
        verify(rideFeignClient).getRideById(request.getRideId());
    }

    @Test
    void rateNotExistingPassenger() {
        RatingCreateRequest request = getRatingRequest();
        Rating rating = getDefaulRating();

        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(rating.getPassenger().getId());
        assertThrows(NotFoundException.class, () -> ratingService.ratePassenger(request, rating.getPassenger().getId()));
    }

    @Test
    void getPassengerRatingWhenPassengerNotFound() {
        doReturn(false)
                .when(passengerRepository)
                .existsById(DEFAULT_ID);
        assertThrows(NotFoundException.class, () -> ratingService.getPassengerRating(DEFAULT_ID));
    }

    @Test
    void getPassengerRatingWhenPassengerExists() {
        PassengerRatingResponse ratingResponse = getPassengerRatingResponse();
        doReturn(true)
                .when(passengerRepository)
                .existsById(DEFAULT_ID);
        doReturn(Optional.of(4.5))
                .when(ratingRepository)
                .getRatingsByPassengerId(DEFAULT_ID);
        PassengerRatingResponse passengerRating = ratingService.getPassengerRating(DEFAULT_ID);

        assertNotNull(passengerRating);
        verify(ratingRepository).getRatingsByPassengerId(ratingResponse.getPassengerId());
        assertEquals(ratingResponse.getRating(), passengerRating.getRating());
    }

}
