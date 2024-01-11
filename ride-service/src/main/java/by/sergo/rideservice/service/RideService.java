package by.sergo.rideservice.service;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequestDto;
import by.sergo.rideservice.domain.dto.response.RideListResponseDto;
import by.sergo.rideservice.domain.dto.response.RideResponseDto;
import by.sergo.rideservice.domain.enums.Status;
import by.sergo.rideservice.repository.RideRepository;
import by.sergo.rideservice.service.exception.BadRequestException;
import by.sergo.rideservice.service.exception.ExceptionMessageUtil;
import by.sergo.rideservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static by.sergo.rideservice.domain.enums.Status.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideService {

    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public RideResponseDto create(RideCreateUpdateRequestDto dto) {
        var ride = modelMapper.map(dto, Ride.class);
        ride.setCreatingTime(LocalDateTime.now());
        ride.setPrice(getPrice());
        var savedRide = rideRepository.save(ride);
        return modelMapper.map(savedRide, RideResponseDto.class);
    }

    public RideResponseDto getById(String id) {
        return modelMapper.map(getByIdOrElseThrow(id), RideResponseDto.class);
    }

    @Transactional
    public void deleteById(String id) {
        getByIdOrElseThrow(id);
        rideRepository.deleteById(id);
    }

    @Transactional
    public RideResponseDto update(RideCreateUpdateRequestDto dto, String id) {
        var ride = getByIdOrElseThrow(id);
        var mappedRide = modelMapper.map(dto, Ride.class);
        mappedRide.setId(new ObjectId(id));
        mappedRide.setPrice(getPrice());
        var savedRide = rideRepository.save(mappedRide);
        return modelMapper.map(savedRide, RideResponseDto.class);
    }

    @Transactional
    public RideResponseDto setDriverAndAcceptRide(RideResponseDto dto, Long driverId) {
        var ride = getByIdOrElseThrow(dto.getId());
        ride.setDriverId(driverId);
        ride.setStatus(ACCEPTED);
        var savedRide = rideRepository.save(modelMapper.map(ride, Ride.class));
        return modelMapper.map(savedRide, RideResponseDto.class);
    }

    @Transactional
    public void rejectRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        ride.setStatus(REJECTED);
        rideRepository.save(ride);
    }

    @Transactional
    public void startRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        ride.setStatus(TRANSPORT);
        ride.setStartTime(LocalDateTime.now());
        rideRepository.save(ride);
    }

    @Transactional
    public void endRide(String rideId) {
        var ride = getByIdOrElseThrow(rideId);
        ride.setStatus(FINISHED);
        ride.setEndTime(LocalDateTime.now());
        rideRepository.save(ride);
    }

    public RideListResponseDto getByPassengerId(Long passengerId, String status, Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);

        if (!Arrays.stream(values()).map(Enum::toString).toList().contains(status.toUpperCase())) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidStatusMessage(status));
        }

        var responsePage = rideRepository.findAllByPassengerIdAndStatus(passengerId, Status.valueOf(status), pageRequest)
                .map(ride -> modelMapper.map(ride, RideResponseDto.class));
        return RideListResponseDto.builder()
                .rides(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    public RideListResponseDto getByDriverId(Long driverId, String status, Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);

        if (!Arrays.stream(values()).map(Enum::toString).toList().contains(status.toUpperCase())) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidStatusMessage(status));
        }

        var responsePage = rideRepository.findAllByDriverIdAndStatus(driverId, Status.valueOf(status), pageRequest)
                .map(ride -> modelMapper.map(ride, RideResponseDto.class));
        return RideListResponseDto.builder()
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
        }

        if (field != null) {
            List<String> declaredFields = Arrays.stream(RideResponseDto.class.getDeclaredFields())
                    .map(Field::getName)
                    .toList();
            if (!declaredFields.contains(field.toLowerCase())) {
                throw new BadRequestException(ExceptionMessageUtil.getInvalidSortingParamRequestMessage(field));
            }
            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc(field.toLowerCase())));
        }
        if (field == null) {
            return PageRequest.of(page - 1, size);
        } else return PageRequest.of(0, 10);
    }

    private Double getPrice() {
        double start = 2.70;
        double end = 30.00;
        double random = new Random().nextDouble();
        return Math.floor((start + (random * (end - start)))*100)/100;
    }

    private Ride getByIdOrElseThrow(String id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessageUtil.getNotFoundMessage("Ride", "id", id)));
    }
}
