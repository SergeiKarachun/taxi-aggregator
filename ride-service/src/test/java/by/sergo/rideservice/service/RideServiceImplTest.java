package by.sergo.rideservice.service;

import by.sergo.rideservice.client.DriverFeignClient;
import by.sergo.rideservice.client.PassengerFeignClient;
import by.sergo.rideservice.client.PaymentFeignClient;
import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.DriverForRideResponse;
import by.sergo.rideservice.domain.dto.request.EditDriverStatusRequest;
import by.sergo.rideservice.domain.dto.request.FindDriverForRideRequest;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.CreditCardResponse;
import by.sergo.rideservice.domain.dto.response.PassengerResponse;
import by.sergo.rideservice.domain.dto.response.RideListResponse;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import by.sergo.rideservice.domain.enums.Status;
import by.sergo.rideservice.kafka.RideProducer;
import by.sergo.rideservice.kafka.StatusProducer;
import by.sergo.rideservice.mapper.RideMapper;
import by.sergo.rideservice.repository.RideRepository;
import by.sergo.rideservice.service.exception.BadRequestException;
import by.sergo.rideservice.service.exception.NotFoundException;
import by.sergo.rideservice.service.impl.RideServiceImpl;
import by.sergo.rideservice.util.RideTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static by.sergo.rideservice.domain.enums.PaymentMethod.CASH;
import static by.sergo.rideservice.domain.enums.Status.*;
import static by.sergo.rideservice.util.RideTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceImplTest {

    @Mock
    private RideRepository rideRepository;
    @Mock
    private RideMapper rideMapper;
    @Mock
    private PaymentFeignClient paymentFeignClient;
    @Mock
    private DriverFeignClient driverFeignClient;
    @Mock
    private PassengerFeignClient passengerFeignClient;
    @Mock
    private StatusProducer statusProducer;
    @Mock
    private RideProducer rideProducer;

    @InjectMocks
    private RideServiceImpl rideService;

    @Test
    void createRideWithPaymentMethodCash() {
        RideCreateUpdateRequest request = getRideCreateRequest();
        request.setPaymentMethod(CASH.name());
        Ride createdRide = getDefaultRideToSave();
        RideResponse expected = getDefaultRideResponse();
        Ride savedRide = getDefaultRide();
        savedRide.setPaymentMethod(CASH);
        PassengerResponse passengerResponse = getDefaultPassengerResponse();
        expected.setPassenger(passengerResponse);

        doReturn(createdRide)
                .when(rideMapper)
                .mapToEntity(request);
        doReturn(savedRide)
                .when(rideRepository)
                .save(createdRide);
        doReturn(expected)
                .when(rideMapper)
                .mapToDto(savedRide);
        doReturn(getDefaultPassengerResponse())
                .when(passengerFeignClient)
                .getPassengerById(DEFAULT_ID);

        RideResponse actual = rideService.create(request);


        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(passengerFeignClient, times(1)).getPassengerById(DEFAULT_PASSENGER_ID);
        verify(driverFeignClient, never()).getDriverById(DEFAULT_ID);
        verify(paymentFeignClient, never()).getPassengerCreditCard(DEFAULT_PASSENGER_ID);
        verify(rideProducer).sendMessage(any(FindDriverForRideRequest.class));
    }

    @Test
    void createRideWithPaymentMethodCard() {
        RideCreateUpdateRequest request = getRideCreateRequest();
        CreditCardResponse creditCardResponse = getCreditCardResponse();
        Ride createdRide = getDefaultRideToSave();
        RideResponse expected = getDefaultRideResponse();
        Ride savedRide = getDefaultRide();
        PassengerResponse passengerResponse = getDefaultPassengerResponse();
        expected.setPassenger(passengerResponse);

        doReturn(creditCardResponse)
                .when(paymentFeignClient)
                .getPassengerCreditCard(DEFAULT_PASSENGER_ID);
        doReturn(createdRide)
                .when(rideMapper)
                .mapToEntity(request);
        doReturn(savedRide)
                .when(rideRepository)
                .save(createdRide);
        doReturn(expected)
                .when(rideMapper)
                .mapToDto(savedRide);
        doReturn(getDefaultPassengerResponse())
                .when(passengerFeignClient)
                .getPassengerById(DEFAULT_ID);

        RideResponse actual = rideService.create(request);


        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(passengerFeignClient, times(1)).getPassengerById(DEFAULT_PASSENGER_ID);
        verify(driverFeignClient, never()).getDriverById(DEFAULT_ID);
        verify(paymentFeignClient).getPassengerCreditCard(DEFAULT_PASSENGER_ID);
        verify(rideProducer).sendMessage(any(FindDriverForRideRequest.class));
    }

    @Test
    void createRideWithPaymentMethodCardWhenBalanceCreditCardIsInvalid() {
        RideCreateUpdateRequest request = getRideCreateRequest();
        CreditCardResponse creditCardResponse = getCreditCardResponse();
        creditCardResponse.setBalance(BigDecimal.ZERO);

        doReturn(creditCardResponse)
                .when(paymentFeignClient)
                .getPassengerCreditCard(DEFAULT_PASSENGER_ID);

        assertThrows(BadRequestException.class, () -> rideService.create(request));

        verify(passengerFeignClient, times(1)).getPassengerById(DEFAULT_PASSENGER_ID);
        verify(driverFeignClient, never()).getDriverById(DEFAULT_ID);
        verify(paymentFeignClient).getPassengerCreditCard(DEFAULT_PASSENGER_ID);
        verify(rideProducer, never()).sendMessage(any(FindDriverForRideRequest.class));
    }

    @Test
    void getRideById() {
        RideResponse expected = getDefaultRideResponse();
        Ride ride = getDefaultRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);
        doReturn(expected)
                .when(rideMapper)
                .mapToDto(ride);

        RideResponse actual = rideService.getById(DEFAULT_RIDE_ID);

        assertEquals(expected, actual);
        verify(driverFeignClient, never()).getDriverById(DEFAULT_ID);
        verify(paymentFeignClient, never()).getPassengerCreditCard(DEFAULT_PASSENGER_ID);
        verify(rideProducer, never()).sendMessage(any(FindDriverForRideRequest.class));
    }

    @Test
    void getNotExistingRideById() {
        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(NotFoundException.class, () -> rideService.getById(DEFAULT_RIDE_ID));

        verify(passengerFeignClient, never()).getPassengerById(DEFAULT_PASSENGER_ID);
        verify(driverFeignClient, never()).getDriverById(DEFAULT_ID);
    }

    @Test
    void deleteRideById() {
        Ride ride = getDefaultRide();

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        rideService.deleteById(DEFAULT_RIDE_ID);

        verify(rideRepository, times(1)).findById(DEFAULT_RIDE_ID);
        verify(rideRepository, times(1)).deleteById(DEFAULT_RIDE_ID);
    }

    @Test
    void deleteRideByIdWhenRideNotExist() {
        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(NotFoundException.class, () -> rideService.deleteById(DEFAULT_RIDE_ID));

        verify(rideRepository, times(1)).findById(DEFAULT_RIDE_ID);
        verify(rideRepository, never()).deleteById(DEFAULT_RIDE_ID);
    }

    @Test
    void updateRide() {
        Ride ride = getDefaultRideToSave();
        RideCreateUpdateRequest updateRequest = getRideUpdateRequest();
        Ride savedRide = getDefaultRide();
        RideResponse response = getDefaultRideResponse();

        doReturn(Optional.of(savedRide))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);
        doReturn(ride)
                .when(rideMapper)
                .mapToEntity(updateRequest);
        doReturn(savedRide)
                .when(rideRepository)
                .save(ride);
        doReturn(response)
                .when(rideMapper)
                .mapToDto(savedRide);

        rideService.update(updateRequest, DEFAULT_RIDE_ID);

        verify(rideRepository).findById(DEFAULT_RIDE_ID);
        verify(rideMapper).mapToEntity(updateRequest);
        verify(rideRepository).save(ride);
        verify(rideMapper).mapToDto(savedRide);
        verify(passengerFeignClient, times(1)).getPassengerById(DEFAULT_PASSENGER_ID);
        verify(driverFeignClient, never()).getDriverById(DEFAULT_ID);
    }

    @Test
    void updateRideWhenRideNotExist() {
        RideCreateUpdateRequest updateRequest = getRideUpdateRequest();

        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(NotFoundException.class, () -> rideService.update(updateRequest, DEFAULT_RIDE_ID));

        verify(rideRepository, times(1)).findById(DEFAULT_RIDE_ID);
        verify(rideRepository, never()).deleteById(DEFAULT_RIDE_ID);
    }

    @Test
    void sendEditStatusWhenRideExists() {
        DriverForRideResponse response = getDriverForRideResponse();
        EditDriverStatusRequest statusRequest = EditDriverStatusRequest.builder()
                .driverId(response.getDriverId())
                .build();

        doReturn(Optional.of(getDefaultRide()))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);


        rideService.sendEditStatus(response);

        verify(rideRepository).findById(DEFAULT_RIDE_ID);
        verify(rideRepository).save(any(Ride.class));
        verify(statusProducer).sendMessage(statusRequest);
    }

    @Test
    void sendEditStatusWhenRideNotFount() {
        DriverForRideResponse response = getDriverForRideResponse();

        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(NotFoundException.class, () -> rideService.sendEditStatus(response));

        verify(rideRepository, times(1)).findById(DEFAULT_RIDE_ID);
        verify(statusProducer, never()).sendMessage(any(EditDriverStatusRequest.class));
    }

    @Test
    void rejectRideWhenRideNotExist() {
        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(NotFoundException.class, () -> rideService.rejectRide(DEFAULT_RIDE_ID));
    }

    @Test
    void rejectRideWhenRideExistAndStatusIsNotCreated() {
        Ride ride = getDefaultRideToSave();
        ride.setStatus(Status.ACCEPTED);

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(BadRequestException.class, () -> rideService.rejectRide(DEFAULT_RIDE_ID));
    }

    @Test
    void rejectRideWhenRideExist() {
        Ride ride = getDefaultRideToSave();
        RideResponse response = getDefaultRideResponse();
        response.setStatus(REJECTED);

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);
        doReturn(ride)
                .when(rideRepository)
                .save(ride);
        doReturn(response)
                .when(rideMapper)
                .mapToDto(ride);

        RideResponse actual = rideService.rejectRide(DEFAULT_RIDE_ID);

        assertEquals(REJECTED, actual.getStatus());
    }

    @Test
    void startRideWhenRideNotExist() {
        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(NotFoundException.class, () -> rideService.startRide(DEFAULT_RIDE_ID));
    }

    @Test
    void startRideWhenRideExistAndStatusIsNotAccepted() {
        Ride ride = getDefaultRideToSave();
        ride.setStatus(CREATED);

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(BadRequestException.class, () -> rideService.startRide(DEFAULT_RIDE_ID));
    }

    @Test
    void startRideWhenRideExist() {
        Ride ride = getDefaultRideToSave();
        ride.setStatus(ACCEPTED);
        RideResponse response = getDefaultRideResponse();
        response.setStatus(TRANSPORT);

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);
        doReturn(ride)
                .when(rideRepository)
                .save(ride);
        doReturn(response)
                .when(rideMapper)
                .mapToDto(ride);

        RideResponse actual = rideService.startRide(DEFAULT_RIDE_ID);

        assertEquals(TRANSPORT, actual.getStatus());
    }

    @Test
    void endRideWhenRideNotExist() {
        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(NotFoundException.class, () -> rideService.endRide(DEFAULT_RIDE_ID));
    }

    @Test
    void endRideWhenRideExistAndStatusIsNotTransport() {
        Ride ride = getDefaultRideToSave();
        ride.setStatus(FINISHED);

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);

        assertThrows(BadRequestException.class, () -> rideService.endRide(DEFAULT_RIDE_ID));
    }

    @Test
    void endRideWhenRideExist() {
        Ride ride = getDefaultRideToSave();
        ride.setStatus(TRANSPORT);
        RideResponse response = getDefaultRideResponse();
        response.setStatus(FINISHED);

        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_RIDE_ID);
        doReturn(ride)
                .when(rideRepository)
                .save(ride);
        doReturn(response)
                .when(rideMapper)
                .mapToDto(ride);

        RideResponse actual = rideService.endRide(DEFAULT_RIDE_ID);

        assertEquals(FINISHED, actual.getStatus());
    }

    @Test
    void getRidesByPassengerId() {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(
                getDefaultRide(),
                getSecondRide()
        ));

        when(rideRepository.findAllByPassengerIdAndStatus(DEFAULT_PASSENGER_ID, DEFAULT_STATUS, PageRequest.of(VALID_PAGE - 1, VALID_SIZE).withSort(Sort.by(Sort.Order.asc(VALID_ORDER_BY))))).thenReturn(ridePage);
        doReturn(getDefaultRideResponse()).when(rideMapper).mapToDto(any(Ride.class));

        RideListResponse actual = rideService.getByPassengerId(DEFAULT_PASSENGER_ID, DEFAULT_STATUS.toString(), VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(actual);
        assertEquals(2, actual.getRides().size());
        verify(rideMapper, times(2)).mapToDto(any(Ride.class));
    }

    @Test
    void getRidesByPassengerIdWithInvalidParams() {
        assertThrows(BadRequestException.class, () -> rideService.getByPassengerId(DEFAULT_PASSENGER_ID, DEFAULT_STATUS.toString(), INVALID_PAGE, INVALID_SIZE, INVALID_ORDER_BY));
    }

    @Test
    void getRidesByDriverId() {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(
                RideTestUtil.getFinishedRide()
        ));

        when(rideRepository.findAllByDriverIdAndStatus(DEFAULT_ID, DEFAULT_STATUS, PageRequest.of(VALID_PAGE - 1, VALID_SIZE).withSort(Sort.by(Sort.Order.asc(VALID_ORDER_BY))))).thenReturn(ridePage);
        doReturn(getDefaultRideResponse()).when(rideMapper).mapToDto(any(Ride.class));

        RideListResponse actual = rideService.getByDriverId(DEFAULT_ID, DEFAULT_STATUS.toString(), VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(actual);
        assertEquals(1, actual.getRides().size());
        verify(rideMapper, times(1)).mapToDto(any(Ride.class));
    }

    @Test
    void getRidesByDriverIdWithInvalidParams() {
        assertThrows(BadRequestException.class, () -> rideService.getByDriverId(DEFAULT_ID, DEFAULT_STATUS.toString(), INVALID_PAGE, INVALID_SIZE, INVALID_ORDER_BY));
    }
}