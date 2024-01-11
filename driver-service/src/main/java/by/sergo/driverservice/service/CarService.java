package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequestDto;
import by.sergo.driverservice.domain.dto.response.CarListResponseDto;
import by.sergo.driverservice.domain.dto.response.CarResponseDto;
import by.sergo.driverservice.domain.entity.Car;
import by.sergo.driverservice.repository.CarRepository;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.ExceptionMessageUtil;
import by.sergo.driverservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CarResponseDto create(CarCreateUpdateRequestDto dto) {
        checkCarIsUnique(dto);
        checkDriverIsExist(dto.getDriverId());
        checkDriverHasCar(dto.getDriverId());
        checkDate(dto.getYearOfManufacture());
        return Optional.of(mapToEntity(dto))
                .map(carRepository::saveAndFlush)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(HttpStatus.BAD_REQUEST, "Can't create new car, please check input parameters"));
    }

    @Transactional
    public CarResponseDto update(Long id, CarCreateUpdateRequestDto dto) {
        var existCar = getByIdOrElseThrow(id);

        checkCarForUpdate(dto, existCar);

        var carToSave = mapToEntity(dto);
        carToSave.setId(id);

        return Optional.of(carToSave)
                .map(carRepository::saveAndFlush)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(HttpStatus.BAD_REQUEST, "Can't update car, please check input parameters"));
    }

    @Transactional
    public void delete(Long id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
        } else throw new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Car", "id", id));
    }

    public CarResponseDto getById(Long id) {
        var car = getByIdOrElseThrow(id);
        return mapToDto(car);
    }

    public CarResponseDto getByDriverId(Long driverId){
        var car = carRepository.getCarByDriverId(driverId);
        var existCar = car.orElseThrow(() -> new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Car", "driverId", driverId)));
        return mapToDto(existCar);
    }

    public CarListResponseDto getAll(Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        var responsePage = carRepository.findAll(pageRequest)
                .map(this::mapToDto);
        return CarListResponseDto.builder()
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
            throw new BadRequestException(
                    ExceptionMessageUtil.getInvaLidRequestMessage(page, size));
        }

        if (orderBy != null) {
            List<String> declaredFields = Arrays.stream(Car.class.getDeclaredFields())
                    .map(Field::getName)
                    .toList();
            if (!declaredFields.contains(orderBy.toLowerCase())){
                throw new BadRequestException(ExceptionMessageUtil.getInvalidSortingParamRequestMessage(orderBy));
            }
            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc(orderBy.toLowerCase())));
        }
        if (orderBy == null) {
            return PageRequest.of(page - 1, size);
        }
        else return PageRequest.of(0, 10);
    }

    private void checkDriverHasCar(Long driverId) {
        if (carRepository.existsByDriverId(driverId)) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Driver with id=" + driverId + " already has car!");
        }
    }

    private void checkCarForUpdate(CarCreateUpdateRequestDto dto, Car existCar) {
        if (!Objects.equals(dto.getNumber(), existCar.getNumber())) {
            checkCarIsUnique(dto);
        }

        checkDriverIsExist(dto.getDriverId());
        //  checkDriverHasCar(dto.getDriverId());
        checkDate(dto.getYearOfManufacture());
    }

    private void checkDate(Integer yearOfManufacture) {
        if (yearOfManufacture > LocalDate.now().getYear()) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "The year of manufacture should be no more than now.");
        }
    }

    private void checkDriverIsExist(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Can't create new car, driver with id" + driverId + " doesn't exist.");
        }
    }

    private Car getByIdOrElseThrow(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Car", "id", id)));
    }

    private void checkCarIsUnique(CarCreateUpdateRequestDto dto) {
        if (carRepository.existsByNumber(dto.getNumber())) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getAlreadyExistMessage("Car", "number", dto.getNumber()));
        }
    }

    public CarResponseDto mapToDto(Car car) {
        return modelMapper.map(car, CarResponseDto.class);
    }

    public Car mapToEntity(CarCreateUpdateRequestDto dto) {
        return modelMapper.map(dto, Car.class);
    }
}
