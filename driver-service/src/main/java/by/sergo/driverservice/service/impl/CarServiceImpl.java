package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.CarListResponse;
import by.sergo.driverservice.domain.dto.response.CarResponse;
import by.sergo.driverservice.domain.entity.Car;
import by.sergo.driverservice.repository.CarRepository;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.service.CarService;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.ExceptionMessageUtil;
import by.sergo.driverservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public CarResponse create(CarCreateUpdateRequest dto) {
        checkCarIsUnique(dto);
        checkDriverIsExist(dto.getDriverId());
        checkDriverAlreadyHasCar(dto.getDriverId());
        checkDate(dto.getYearOfManufacture());
        return Optional.of(mapToEntity(dto))
                .map(carRepository::save)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getCanNotCreateCarMessage()));
    }

    @Override
    @Transactional
    public CarResponse update(Long id, CarCreateUpdateRequest dto) {
        var existCar = getByIdOrElseThrow(id);
        checkCarForUpdate(dto, existCar);
        var carToSave = mapToEntity(dto);
        carToSave.setId(id);
        return Optional.of(carToSave)
                .map(carRepository::save)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getCanNotUpdateCarMessage()));
    }

    @Override
    @Transactional
    public CarResponse delete(Long id) {
        var car = getByIdOrElseThrow(id);
        carRepository.deleteById(id);
        return mapToDto(car);
    }

    @Override
    public CarResponse getById(Long id) {
        var car = getByIdOrElseThrow(id);
        return mapToDto(car);
    }

    @Override
    public CarResponse getByDriverId(Long driverId){
        var car = carRepository.getCarByDriverId(driverId);
        var existCar = car
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Car", "driverId", driverId)));
        return mapToDto(existCar);
    }

    @Override
    public CarListResponse getAll(Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        var responsePage = carRepository.findAll(pageRequest)
                .map(this::mapToDto);
        return CarListResponse.builder()
                .cars(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    private PageRequest getPageRequest(Integer page, Integer size, String orderBy) {
        if (page < 1 || size < 1) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidRequestMessage(page, size));
        } else if (orderBy != null) {
            Arrays.stream(Car.class.getDeclaredFields())
                    .map(Field::getName)
                    .filter(s -> s.contains(orderBy.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getInvalidSortingParamRequestMessage(orderBy)));
            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc(orderBy.toLowerCase())));
        } else {
            return PageRequest.of(page - 1, size);
        }
    }

    private void checkDriverAlreadyHasCar(Long driverId) {
        if (carRepository.existsByDriverId(driverId)) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Car", "driverId", driverId.toString()));
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
            throw new BadRequestException(ExceptionMessageUtil.getYearShouldBeMessageMessage());
        }
    }

    private void checkDriverIsExist(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new BadRequestException(ExceptionMessageUtil.getNotFoundMessage("Driver", "driverId", driverId));
        }
    }

    private Car getByIdOrElseThrow(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Car", "id", id)));
    }

    private void checkCarIsUnique(CarCreateUpdateRequest dto) {
        if (carRepository.existsByNumber(dto.getNumber())) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Car", "number", dto.getNumber()));
        }
    }

    public CarResponse mapToDto(Car car) {
        return modelMapper.map(car, CarResponse.class);
    }

    public Car mapToEntity(CarCreateUpdateRequest dto) {
        return modelMapper.map(dto, Car.class);
    }
}
