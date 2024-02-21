package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.domain.dto.response.DriverListResponse;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.enums.Status;
import by.sergo.driverservice.mapper.DriverMapper;
import by.sergo.driverservice.service.DriverService;
import by.sergo.driverservice.util.ExceptionMessageUtil;
import by.sergo.driverservice.util.PageRequestUtil;
import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.request.DriverForRideResponse;
import by.sergo.driverservice.domain.dto.request.FindDriverForRideRequest;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.kafka.producer.DriverProducer;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static by.sergo.driverservice.util.ExceptionMessageUtil.CAN_NOT_CREATE_DRIVER_MESSAGE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final DriverProducer driverProducer;

    @Override
    @Transactional
    public DriverResponse create(DriverCreateUpdateRequest dto) {
        checkIsDriverUnique(dto);
        return Optional.of(driverMapper.mapToEntity(dto))
                .map(driverRepository::save)
                .map(driverMapper::mapToDto)
                .orElseThrow(() -> new BadRequestException(CAN_NOT_CREATE_DRIVER_MESSAGE));
    }

    @Override
    @Transactional
    public DriverResponse update(Long id, DriverCreateUpdateRequest dto) {
        var existDriver = getByIdOrElseThrow(id);
        checkIsDriverForUpdateUnique(dto, existDriver);
        var driverToSave = driverMapper.mapToEntity(dto);
        driverToSave.setId(id);
        return driverMapper.mapToDto(driverRepository.save(driverToSave));
    }

    @Override
    @Transactional
    public DriverResponse delete(Long id) {
        var driver = getByIdOrElseThrow(id);
        driverRepository.deleteById(id);
        return driverMapper.mapToDto(driver);
    }

    @Override
    public DriverResponse getById(Long id) {
        var driver = getByIdOrElseThrow(id);
        return driverMapper.mapToDto(driver);
    }

    @Override
    public DriverListResponse getAvailableDrivers(Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = PageRequestUtil.getPageRequest(page, size, orderBy, DriverResponse.class);
        var responsePage = driverRepository.getAllByStatus(Status.AVAILABLE, pageRequest)
                .map(driverMapper::mapToDto);
        return DriverListResponse.builder()
                .drivers(responsePage.getContent())
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    @Override
    public DriverListResponse getAll(Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = PageRequestUtil.getPageRequest(page, size, orderBy, DriverResponse.class);
        var responsePage = driverRepository.findAll(pageRequest)
                .map(driverMapper::mapToDto);
        return DriverListResponse.builder()
                .drivers(responsePage.getContent())
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    @Override
    @Transactional
    public DriverResponse changeStatus(Long id) {
        var driver = getByIdOrElseThrow(id);
        if (driver.getStatus().equals(Status.AVAILABLE)) {
            driver.setStatus(Status.UNAVAILABLE);
        } else {
            driver.setStatus(Status.AVAILABLE);
            driverProducer.sendMessage(DriverForRideResponse.builder()
                    .driverId(driver.getId())
                    .rideId("free")
                    .build());
        }
        return driverMapper.mapToDto(driverRepository.save(driver));
    }

    @Override
    public void handleDriverForRide(FindDriverForRideRequest request) {
        Optional<Driver> driverForRide = driverRepository.findFirstByStatusOrderByRating();
        if (driverForRide.isPresent()) {
            DriverForRideResponse driver = DriverForRideResponse.builder()
                    .driverId(driverForRide.get().getId())
                    .rideId(request.getRideId())
                    .build();
            driverProducer.sendMessage(driver);
        }
    }

    private void checkIsDriverForUpdateUnique(DriverCreateUpdateRequest dto, Driver existDriver) {
        var errors = new HashMap<String, String>();
        if (!Objects.equals(dto.getEmail(), existDriver.getEmail())) {
            checkIsEmailUnique(dto.getEmail(), errors);
        }

        if (!Objects.equals(dto.getPhone(), existDriver.getPhone())) {
            checkIsPhoneUnique(dto.getPhone(), errors);
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMapMessage(errors));
        }
    }

    private Driver getByIdOrElseThrow(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Driver", "id", id)));
    }

    private void checkIsDriverUnique(DriverCreateUpdateRequest dto) {
        var errors = new HashMap<String, String>();
        checkIsEmailUnique(dto.getEmail(), errors);
        checkIsPhoneUnique(dto.getPhone(), errors);

        if (!errors.isEmpty()) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMapMessage(errors));
        }
    }

    private void checkIsPhoneUnique(String phone, Map<String, String> errors) {
        if (driverRepository.existsByPhone(phone)) {
            errors.put("phone", ExceptionMessageUtil.getAlreadyExistMessage("Driver", "phone", phone));
        }
    }

    private void checkIsEmailUnique(String email, Map<String, String> errors) {
        if (driverRepository.existsByEmail(email)) {
            errors.put("email", ExceptionMessageUtil.getAlreadyExistMessage("Driver", "email", email));
        }
    }
}
