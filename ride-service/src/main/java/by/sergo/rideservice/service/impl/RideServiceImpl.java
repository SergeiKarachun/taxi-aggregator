package by.sergo.rideservice.service.impl;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.DriverRequest;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.RideListResponse;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import by.sergo.rideservice.domain.enums.Status;
import by.sergo.rideservice.repository.RideRepository;
import by.sergo.rideservice.service.RideService;
import by.sergo.rideservice.service.exception.BadRequestException;
import by.sergo.rideservice.service.exception.ExceptionMessageUtil;
import by.sergo.rideservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

import static by.sergo.rideservice.domain.enums.Status.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RideResponse create(RideCreateUpdateRequest dto) {
        var ride = mapToEntity(dto);
        ride.setCreatingTime(LocalDateTime.now());
        ride.setPrice(getPrice());
        var savedRide = rideRepository.save(ride);
        return mapToDto(savedRide);
    }

    @Override
    public RideResponse getById(String id) {
        return mapToDto(getByIdOrElseThrow(id));
    }

    @Override
    @Transactional
    public RideResponse deleteById(String id) {
        var ride = getByIdOrElseThrow(id);
        rideRepository.deleteById(id);
        return mapToDto(ride);
    }

    @Override
    @Transactional
    public RideResponse update(RideCreateUpdateRequest dto, String id) {
        var ride = getByIdOrElseThrow(id);
        var mappedRide = mapToEntity(dto);
        mappedRide.setId(ride.getId());
        mappedRide.setPrice(getPrice());
        var savedRide = rideRepository.save(mappedRide);
        return mapToDto(savedRide);
    }

    @Override
    @Transactional
    public RideResponse setDriverAndAcceptRide(DriverRequest dto, String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        if (ride.getStatus().equals(CREATED) && ride.getDriverId() == null) {
            ride.setDriverId(dto.getDriverId());
            ride.setStatus(ACCEPTED);
        } else throw new BadRequestException(ExceptionMessageUtil.alreadyHasDriver(rideId));
        var savedRide = rideRepository.save(ride);
        return mapToDto(savedRide);
    }

    @Override
    @Transactional
    public RideResponse rejectRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        if (ride.getStatus().equals(CREATED)) {
            ride.setStatus(REJECTED);
        } else throw new BadRequestException(ExceptionMessageUtil.canNotChangeStatusMessage(REJECTED.toString(), ride.getStatus().toString(), CREATED.toString()));
        return mapToDto(rideRepository.save(ride));
    }

    @Override
    @Transactional
    public RideResponse startRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        if (ride.getStatus().equals(ACCEPTED)) {
            ride.setStatus(TRANSPORT);
            ride.setStartTime(LocalDateTime.now());
        } else throw new BadRequestException(ExceptionMessageUtil.canNotChangeStatusMessage(TRANSPORT.toString(), ride.getStatus().toString(), ACCEPTED.toString()));
        return mapToDto(rideRepository.save(ride));
    }

    @Override
    @Transactional
    public RideResponse endRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        if (ride.getStatus().equals(TRANSPORT)) {
            ride.setStatus(FINISHED);
            ride.setEndTime(LocalDateTime.now());
        } else throw new BadRequestException(ExceptionMessageUtil.canNotChangeStatusMessage(FINISHED.toString(), ride.getStatus().toString(), TRANSPORT.toString()));
        return mapToDto(rideRepository.save(ride));
    }

    @Override
    public RideListResponse getByPassengerId(Long passengerId, String status, Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);

        if (!Arrays.stream(values()).map(Enum::toString).toList().contains(status.toUpperCase())) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidStatusMessage(status));
        }

        var responsePage = rideRepository.findAllByPassengerIdAndStatus(passengerId, Status.valueOf(status.toUpperCase()), pageRequest)
                .map(ride -> mapToDto(ride));
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

        if (!Arrays.stream(values()).map(Enum::toString).toList().contains(status.toUpperCase())) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidStatusMessage(status));
        }

        var responsePage = rideRepository.findAllByDriverIdAndStatus(driverId, Status.valueOf(status.toUpperCase()), pageRequest)
                .map(ride -> mapToDto(ride));
        return RideListResponse.builder()
                .rides(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    private PageRequest getPageRequest(Integer page, Integer size, String field) {
        if (page < 1 || size < 1) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidRequestMessage(page, size));
        } else if (field != null) {
            Arrays.stream(RideResponse.class.getDeclaredFields())
                    .map(Field::getName)
                    .filter(s -> s.contains(field.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getInvalidSortingParamRequestMessage(field)));

            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc(field.toLowerCase())));
        } else {
            return PageRequest.of(page - 1, size);
        }
    }

    private Double getPrice() {
        double start = 2.70;
        double end = 30.00;
        double random = new Random().nextDouble();
        return Math.floor((start + (random * (end - start)))*100)/100;
    }

    private Ride getByIdOrElseThrow(String id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Ride", "id", id)));
    }

    private RideResponse mapToDto(Ride ride) {
        return modelMapper.map(ride, RideResponse.class);
    }

    private Ride mapToEntity(RideCreateUpdateRequest dto) {
        return modelMapper.map(dto, Ride.class);
    }
}
