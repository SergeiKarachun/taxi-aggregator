package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.DriverRatingResponse;
import by.sergo.driverservice.domain.dto.response.PassengerResponse;
import by.sergo.driverservice.domain.dto.response.RatingResponse;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import by.sergo.driverservice.domain.entity.Rating;
import by.sergo.driverservice.mapper.RatingMapper;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.repository.RatingRepository;
import by.sergo.driverservice.service.exception.NotFoundException;
import by.sergo.driverservice.service.impl.RatingServiceImpl;
import by.sergo.driverservice.util.RatingTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {

    @Mock
    private DriverRepository driverRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private RatingMapper ratingMapper;
    @Mock
    private PassengerService passengerService;
    @Mock
    private RideService rideService;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Test
    void rateExistingDriver() {
        RatingCreateRequest request = RatingTestUtils.getRatingRequest();
        PassengerResponse passengerResponse = RatingTestUtils.getDefaultPassengerResponse();
        RideResponse rideResponse = RatingTestUtils.getDefaultRideResponse();
        Rating ratingToSave = RatingTestUtils.getDefaultRating();
        Rating savedRating = ratingToSave;
        savedRating.setId(RatingTestUtils.DEFAULT_ID);
        RatingResponse response = RatingTestUtils.getDefaultRatingResponse();

        doReturn(Optional.of(RatingTestUtils.getDefaultDriver()))
                .when(driverRepository)
                .findById(RatingTestUtils.DEFAULT_DRIVER_ID);
        doReturn(ratingToSave)
                .when(ratingMapper)
                .mapToEntity(request);
        doReturn(savedRating)
                .when(ratingRepository)
                .save(ratingToSave);
        doReturn(response)
                .when(ratingMapper)
                .mapToDto(savedRating);
        doReturn(passengerResponse)
                .when(passengerService)
                .getPassenger(request.getPassengerId());
        doReturn(rideResponse)
                .when(rideService)
                .getRide(request.getRideId());

        RatingResponse actual = ratingService.createRateOfDriver(request, RatingTestUtils.DEFAULT_DRIVER_ID);

        assertNotNull(actual);
        verify(driverRepository).findById(RatingTestUtils.DEFAULT_DRIVER_ID);
        verify(ratingRepository).save(ratingToSave);
        verify(ratingMapper).mapToDto(savedRating);
        verify(passengerService, times(1)).getPassenger(request.getPassengerId());
        verify(rideService, times(1)).getRide(request.getRideId());
    }

    @Test
    void getDriverRatingWhenDriverNotFound() {
        doReturn(false)
                .when(driverRepository)
                .existsById(RatingTestUtils.DEFAULT_DRIVER_ID);

        assertThrows(NotFoundException.class, () -> ratingService.getDriverRating(RatingTestUtils.DEFAULT_DRIVER_ID));
    }

    @Test
    void getDriverRatingWhenDriverExists() {
        DriverRatingResponse ratingResponse = RatingTestUtils.getDriverRatingResponse();

        doReturn(true)
                .when(driverRepository)
                .existsById(RatingTestUtils.DEFAULT_DRIVER_ID);
        doReturn(Optional.of(RatingTestUtils.AVERAGE_GRADE))
                .when(ratingRepository)
                .getRatingsByDriverId(RatingTestUtils.DEFAULT_DRIVER_ID);

        DriverRatingResponse driverRatingResponse = ratingService.getDriverRating(RatingTestUtils.DEFAULT_DRIVER_ID);

        assertNotNull(driverRatingResponse);
        verify(ratingRepository).getRatingsByDriverId(ratingResponse.getDriverId());
        assertEquals(ratingResponse.getRating(), driverRatingResponse.getRating());
    }
}
