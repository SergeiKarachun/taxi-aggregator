package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.CarListResponse;
import by.sergo.driverservice.domain.dto.response.CarResponse;
import by.sergo.driverservice.domain.entity.Car;
import by.sergo.driverservice.mapper.CarMapper;
import by.sergo.driverservice.repository.CarRepository;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.service.CarService;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.NotFoundException;
import by.sergo.driverservice.util.PageRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static by.sergo.driverservice.util.ConstantUtil.*;
import static by.sergo.driverservice.util.ExceptionMessageUtil.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional
    public CarResponse create(CarCreateUpdateRequest dto) {
        checkCarForCreate(dto);
        return Optional.of(carMapper.mapToEntity(dto))
                .map(carRepository::save)
                .map(car -> {
                    log.info(CREATE_CAR_LOG, car.getId());
                    return carMapper.mapToDto(car);
                })
                .orElseThrow(() -> new BadRequestException(CAN_NOT_CREATE_CAR_MESSAGE));
    }

    @Override
    @Transactional
    public CarResponse update(Long id, CarCreateUpdateRequest dto) {
        var existCar = getByIdOrElseThrow(id);
        checkCarForUpdate(dto, existCar);
        var carToSave = carMapper.mapToEntity(dto);
        carToSave.setId(id);
        return Optional.of(carToSave)
                .map(carRepository::save)
                .map(car -> {
                    log.info(UPDATE_CAR_LOG, id);
                    return carMapper.mapToDto(car);
                })
                .orElseThrow(() -> new BadRequestException(CAN_NOT_UPDATE_CAR_MESSAGE));
    }

    @Override
    @Transactional
    public CarResponse delete(Long id) {
        var car = getByIdOrElseThrow(id);
        carRepository.deleteById(id);
        log.info(DELETE_CAR_LOG, id);
        return carMapper.mapToDto(car);
    }

    @Override
    public CarResponse getById(Long id) {
        var car = getByIdOrElseThrow(id);
        log.info(GET_CAR_LOG, id);
        return carMapper.mapToDto(car);
    }

    @Override
    public CarResponse getByDriverId(Long driverId){
        var car = carRepository.getCarByDriverId(driverId);
        var existCar = car
                .orElseThrow(() -> new NotFoundException(getNotFoundMessage("Car", "driverId", driverId)));
        log.info(GET_CAR_BY_DRIVER_ID_LOG, driverId);
        return carMapper.mapToDto(existCar);
    }

    @Override
    public CarListResponse getAll(Integer page, Integer size, String orderBy) {
        log.info(GET_CARS_LOG);
        PageRequest pageRequest = PageRequestUtil.getPageRequest(page, size, orderBy, Car.class);
        var responsePage = carRepository.findAll(pageRequest)
                .map(carMapper::mapToDto);
        return CarListResponse.builder()
                .cars(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    private void checkCarForCreate(CarCreateUpdateRequest dto) {
        checkCarIsUnique(dto);
        checkDriverIsExist(dto.getDriverId());
        checkDriverAlreadyHasCar(dto.getDriverId());
    }

    private void checkDriverAlreadyHasCar(Long driverId) {
        if (carRepository.existsByDriverId(driverId)) {
            throw new BadRequestException(getAlreadyExistMessage("Car", "driverId", driverId.toString()));
        }
    }

    private void checkCarForUpdate(CarCreateUpdateRequest dto, Car existCar) {
        if (!Objects.equals(dto.getNumber(), existCar.getNumber())) {
            checkCarIsUnique(dto);
        }
        checkDriverIsExist(dto.getDriverId());

        if (!existCar.getDriver().getId().equals(dto.getDriverId())){
            checkDriverAlreadyHasCar(dto.getDriverId());
        }
    }

    private void checkDriverIsExist(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new BadRequestException(getNotFoundMessage("Driver", "driverId", driverId));
        }
    }

    private Car getByIdOrElseThrow(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(getNotFoundMessage("Car", "id", id)));
    }

    private void checkCarIsUnique(CarCreateUpdateRequest dto) {
        if (carRepository.existsByNumber(dto.getNumber())) {
            throw new BadRequestException(getAlreadyExistMessage("Car", "number", dto.getNumber()));
        }
    }
}
