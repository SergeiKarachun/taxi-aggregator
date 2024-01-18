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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static by.sergo.driverservice.util.ExceptionMessageUtil.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
                .map(carMapper::mapToDto)
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
                .map(carMapper::mapToDto)
                .orElseThrow(() -> new BadRequestException(CAN_NOT_UPDATE_CAR_MESSAGE));
    }

    @Override
    @Transactional
    public CarResponse delete(Long id) {
        var car = getByIdOrElseThrow(id);
        carRepository.deleteById(id);
        return carMapper.mapToDto(car);
    }

    @Override
    public CarResponse getById(Long id) {
        var car = getByIdOrElseThrow(id);
        return carMapper.mapToDto(car);
    }

    @Override
    public CarResponse getByDriverId(Long driverId){
        var car = carRepository.getCarByDriverId(driverId);
        var existCar = car
                .orElseThrow(() -> new NotFoundException(getNotFoundMessage("Car", "driverId", driverId)));
        return carMapper.mapToDto(existCar);
    }

    @Override
    public CarListResponse getAll(Integer page, Integer size, String orderBy) {
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
        checkDate(dto.getYearOfManufacture());
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
        checkDate(dto.getYearOfManufacture());
    }

    private void checkDate(Integer yearOfManufacture) {
        if (yearOfManufacture > LocalDate.now().getYear()) {
            throw new BadRequestException(YEAR_SHOULD_BE_MESSAGE);
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
