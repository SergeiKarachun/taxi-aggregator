package by.sergo.rideservice.service.impl;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.*;
import by.sergo.rideservice.domain.dto.response.CreditCardResponse;
import by.sergo.rideservice.domain.dto.response.PassengerResponse;
import by.sergo.rideservice.domain.dto.response.RideListResponse;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import by.sergo.rideservice.domain.enums.Status;
import by.sergo.rideservice.kafka.RideProducer;
import by.sergo.rideservice.kafka.StatusProducer;
import by.sergo.rideservice.mapper.RideMapper;
import by.sergo.rideservice.repository.RideRepository;
import by.sergo.rideservice.service.DriverService;
import by.sergo.rideservice.service.PassengerService;
import by.sergo.rideservice.service.PaymentService;
import by.sergo.rideservice.service.RideService;
import by.sergo.rideservice.service.exception.BadRequestException;
import by.sergo.rideservice.util.ExceptionMessageUtil;
import by.sergo.rideservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static by.sergo.rideservice.domain.enums.PaymentMethod.CARD;
import static by.sergo.rideservice.domain.enums.Status.*;
import static by.sergo.rideservice.util.ConstantUtil.MAX_PRICE;
import static by.sergo.rideservice.util.ConstantUtil.MIN_PRICE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final StatusProducer statusProducer;
    private final RideProducer rideProducer;
    private final PassengerService passengerService;
    private final DriverService driverService;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public RideResponse create(RideCreateUpdateRequest dto) {
        checkPassenger(dto);
        var price = getPrice();
        checkRidePayment(dto, price);
        var ride = rideMapper.mapToEntity(dto);
        ride.setCreatingTime(LocalDateTime.now());
        ride.setPrice(price);
        var savedRide = rideRepository.save(ride);
        rideProducer.sendMessage(FindDriverForRideRequest.builder()
                .rideId(savedRide.getId().toString())
                .build()
        );
        return getRideResponse(savedRide);
    }

    @Override
    public RideResponse getById(String id) {
        Ride ride = getByIdOrElseThrow(id);
        return getRideResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse deleteById(String id) {
        var ride = getByIdOrElseThrow(id);
        rideRepository.deleteById(id);
        return rideMapper.mapToDto(ride);
    }

    @Override
    @Transactional
    public RideResponse update(RideCreateUpdateRequest dto, String id) {
        checkPassenger(dto);
        var price = getPrice();
        checkRidePayment(dto, price);
        var ride = getByIdOrElseThrow(id);
        var mappedRide = rideMapper.mapToEntity(dto);
        mappedRide.setId(ride.getId());
        mappedRide.setPrice(price);
        var savedRide = rideRepository.save(mappedRide);
        return getRideResponse(savedRide);
    }

    @Transactional
    public void setDriverAndAcceptRide(DriverForRideResponse response) {
        checkDriver(response);
        var ride = getByIdOrElseThrow(response.getRideId());

        if (!ride.getStatus().equals(CREATED) || ride.getDriverId() != null) {
            throw new BadRequestException(ExceptionMessageUtil.alreadyHasDriver(response.getRideId()));
        }

        ride.setDriverId(response.getDriverId());
        ride.setStatus(ACCEPTED);
        rideRepository.save(ride);
    }

    @Override
    public void sendEditStatus(DriverForRideResponse response) {
        setDriverAndAcceptRide(response);
        EditDriverStatusRequest driverStatusRequest = EditDriverStatusRequest.builder()
                .driverId(response.getDriverId())
                .build();
        statusProducer.sendMessage(driverStatusRequest);
    }

    @Override
    @Transactional
    public RideResponse rejectRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        if (!ride.getStatus().equals(CREATED)) {
            throw new BadRequestException(ExceptionMessageUtil.canNotChangeStatusMessage(REJECTED.toString(), ride.getStatus().toString(), CREATED.toString()));
        }

        ride.setStatus(REJECTED);
        var savedRide = rideRepository.save(ride);
        return getRideResponse(savedRide);
    }

    @Override
    @Transactional
    public RideResponse startRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        if (!ride.getStatus().equals(ACCEPTED)) {
            throw new BadRequestException(ExceptionMessageUtil.canNotChangeStatusMessage(TRANSPORT.toString(), ride.getStatus().toString(), ACCEPTED.toString()));
        }

        ride.setStartTime(LocalDateTime.now());
        ride.setStatus(TRANSPORT);
        var savedRide = rideRepository.save(ride);
        return getRideResponse(savedRide);
    }

    @Override
    @Transactional
    public RideResponse endRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        if (!ride.getStatus().equals(TRANSPORT)) {
            throw new BadRequestException(ExceptionMessageUtil.canNotChangeStatusMessage(FINISHED.toString(), ride.getStatus().toString(), TRANSPORT.toString()));
        }

        ride.setStatus(FINISHED);
        ride.setEndTime(LocalDateTime.now());
        var savedRide = rideRepository.save(ride);
        statusProducer.sendMessage(EditDriverStatusRequest.builder()
                .driverId(ride.getDriverId())
                .build());
        return getRideResponse(savedRide);
    }

    @Override
    public RideListResponse getByPassengerId(Long passengerId, String status, Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);

        checkStatus(status);

        Page<RideResponse> responsePage = rideRepository.findAllByPassengerIdAndStatus(passengerId, Status.valueOf(status.toUpperCase()), pageRequest)
                .map(this::getRideResponse);
        return RideListResponse.builder()
                .rides(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    @Override
    public RideListResponse getByDriverId(Long driverId, String status, Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);

        checkStatus(status);

        Page<RideResponse> responsePage = rideRepository.findAllByDriverIdAndStatus(driverId, Status.valueOf(status.toUpperCase()), pageRequest)
                .map(this::getRideResponse);
        return RideListResponse.builder()
                .rides(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    public void setDriver(DriverForRideResponse driver) {
        List<Ride> rides = rideRepository.findAll().stream()
                .filter(ride -> ride.getDriverId() == null)
                .toList();
        if (!rides.isEmpty()) {
            Ride rideWithoutDriver = rideRepository.findAll().stream()
                    .filter(ride -> ride.getDriverId() == null)
                    .toList()
                    .get(0);
            rideWithoutDriver.setDriverId(driver.getDriverId());
            rideWithoutDriver.setStatus(Status.ACCEPTED);
            rideRepository.save(rideWithoutDriver);
            statusProducer.sendMessage(EditDriverStatusRequest.builder()
                    .driverId(driver.getDriverId())
                    .build());
        }
    }

    private RideResponse getRideResponse(Ride ride) {
        var rideResponse = rideMapper.mapToDto(ride);
        if (ride.getPassengerId() != null) {
            var passengerResponse = passengerService.getPassenger(ride.getPassengerId());
            rideResponse.setPassenger(passengerResponse);
        }

        if (ride.getDriverId() != null) {
            var driverResponse = driverService.getDriver(ride.getDriverId());
            rideResponse.setDriver(driverResponse);
        }
        return rideResponse;
    }

    private static void checkStatus(String status) {
        Arrays.stream(Status.values())
                .map(Enum::toString)
                .filter(s -> s.contains(status.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getInvalidStatusMessage(status)));
    }

    private PageRequest getPageRequest(Integer page, Integer size, String field) {
        if (page < 1 || size < 1) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidRequestMessage(page, size));
        } else if (field != null) {
            checkFieldToSort(field);

            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc(field.toLowerCase())));
        }
        return PageRequest.of(page - 1, size);
    }

    private static void checkFieldToSort(String field) {
        Arrays.stream(RideResponse.class.getDeclaredFields())
                .map(Field::getName)
                .filter(s -> s.contains(field.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getInvalidSortingParamRequestMessage(field)));
    }

    private CreditCardResponse getPassengerCreditCardById(Long id) {
        return paymentService.getPassengerCreditCard(id);
    }

    private void checkRidePayment(RideCreateUpdateRequest dto, Double price) {
        if (dto.getPaymentMethod().equals(CARD.toString())) {
            var passengerCreditCard = getPassengerCreditCardById(dto.getPassengerId());
            if (passengerCreditCard.getBalance().compareTo(new BigDecimal(price)) < 0) {
                throw new BadRequestException(ExceptionMessageUtil.getNotEnoughMoneyMessage("Passenger", "passengerId", dto.getPassengerId()));
            }
        }
    }

    private PassengerResponse checkPassenger(RideCreateUpdateRequest dto) {
        return passengerService.getPassenger(dto.getPassengerId());
    }

    private void checkDriver(DriverForRideResponse response) {
        driverService.getDriver(response.getDriverId());
    }

    private Double getPrice() {
        double random = new Random().nextDouble();
        return Math.floor((MIN_PRICE + (random * (MAX_PRICE - MIN_PRICE))) * 100) / 100;
    }

    private Ride getByIdOrElseThrow(String id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Ride", "id", id)));
    }
}
