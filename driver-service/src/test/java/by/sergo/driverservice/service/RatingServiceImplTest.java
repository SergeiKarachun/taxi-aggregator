package by.sergo.driverservice.service;

import by.sergo.driverservice.client.PassengerFeignClient;
import by.sergo.driverservice.client.RideFeignClient;
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

import static by.sergo.driverservice.util.RatingTestUtils.*;
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
    private PassengerFeignClient passengerFeignClient;
    @Mock
    private RideFeignClient rideFeignClient;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Test
    void rateExistingDriver() {
        RatingCreateRequest request = getRatingRequest();
        Rating ratingToSave = getDefaultRating();
        Rating savedRating = getDefaultRating();
        RatingResponse response = RatingTestUtils.getDefaultRatingResponse();
        PassengerResponse passengerResponse = RatingTestUtils.getDefaultPassengerResponse();
        RideResponse rideResponse = RatingTestUtils.getDefaultRideResponse();

        doReturn(Optional.of(getDefaultDriver()))
                .when(driverRepository)
                .findById(DEFAULT_DRIVER_ID);
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
                .when(passengerFeignClient)
                .getPassengerById(request.getPassengerId());
        doReturn(rideResponse)
                .when(rideFeignClient)
                .getRideById(request.getRideId());

        RatingResponse actual = ratingService.createRateOfDriver(request, DEFAULT_DRIVER_ID);

        assertNotNull(actual);
        verify(driverRepository).findById(DEFAULT_DRIVER_ID);
        verify(ratingRepository).save(ratingToSave);
        verify(ratingMapper).mapToDto(savedRating);
        verify(passengerFeignClient).getPassengerById(request.getPassengerId());
        verify(rideFeignClient).getRideById(request.getRideId());
    }

    @Test
    void getDriverRatingWhenDriverNotFound() {
        doReturn(false)
                .when(driverRepository)
                .existsById(DEFAULT_DRIVER_ID);

        assertThrows(NotFoundException.class, () -> ratingService.getDriverRating(DEFAULT_DRIVER_ID));
    }

    @Test
    void getDriverRatingWhenDriverExists() {
        DriverRatingResponse ratingResponse = RatingTestUtils.getDriverRatingResponse();

        doReturn(true)
                .when(driverRepository)
                .existsById(DEFAULT_DRIVER_ID);
        doReturn(Optional.of(AVERAGE_GRADE))
                .when(ratingRepository)
                .getRatingsByDriverId(DEFAULT_DRIVER_ID);

        DriverRatingResponse driverRatingResponse = ratingService.getDriverRating(DEFAULT_DRIVER_ID);

        assertNotNull(driverRatingResponse);
        verify(ratingRepository).getRatingsByDriverId(ratingResponse.getDriverId());
        assertEquals(ratingResponse.getRating(), driverRatingResponse.getRating());
    }
}
