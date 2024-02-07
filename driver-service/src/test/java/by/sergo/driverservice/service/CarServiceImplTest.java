package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.CarResponse;
import by.sergo.driverservice.domain.entity.Car;
import by.sergo.driverservice.mapper.CarMapper;
import by.sergo.driverservice.repository.CarRepository;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.service.impl.CarServiceImpl;
import by.sergo.driverservice.util.CarTestUtils;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void createUniqueCar() {
        CarCreateUpdateRequest request = CarTestUtils.getCarCreateRequest();
        CarResponse response = CarTestUtils.getDefaultCarResponse();
        Car carToSave = CarTestUtils.getCarToSave();
        Car savedCar = CarTestUtils.getDefaultCar();

        doReturn(false)
                .when(carRepository)
                .existsByNumber(request.getNumber());
        doReturn(true)
                .when(driverRepository)
                .existsById(request.getDriverId());
        doReturn(false)
                .when(carRepository)
                .existsByDriverId(request.getDriverId());
        doReturn(carToSave)
                .when(carMapper)
                .mapToEntity(request);
        doReturn(savedCar)
                .when(carRepository)
                .save(carToSave);
        doReturn(response)
                .when(carMapper)
                .mapToDto(savedCar);

        CarResponse actual = carService.create(request);

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void createCar_WhenCarNumberNotUnique() {
        CarCreateUpdateRequest request = CarTestUtils.getCarCreateRequest();
        Car existingCar = CarTestUtils.getDefaultCar();

        when(carRepository.existsByNumber(existingCar.getNumber()))
                .thenThrow(BadRequestException.class);

        assertThrows(BadRequestException.class, () -> carService.create(request));

        verify(carRepository, times(1)).existsByNumber(existingCar.getNumber());
    }

    @Test
    void updateCar_WhenCarExistsAndNumberUnique() {
        CarCreateUpdateRequest request = CarTestUtils.getCarUpdateRequest();
        Car car = CarTestUtils.getDefaultCar();
        Car updateCar = CarTestUtils.getUpdatedCar();
        CarResponse expected = CarTestUtils.getDefaultCarResponse();


        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(CarTestUtils.DEFAULT_ID);
        doReturn(true)
                .when(driverRepository)
                .existsById(car.getDriver().getId());
        doReturn(false)
                .when(carRepository)
                .existsByNumber(request.getNumber());
        doReturn(updateCar)
                .when(carMapper)
                .mapToEntity(request);
        doReturn(updateCar)
                .when(carRepository)
                .save(updateCar);
        doReturn(expected)
                .when(carMapper)
                .mapToDto(updateCar);

        CarResponse actual = carService.update(car.getId(), request);

        assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(car.getId());
        verify(carRepository, times(1)).save(updateCar);
    }

    @Test
    void updateCar_WhenCarNumberAlreadyExists() {
        CarCreateUpdateRequest request = CarTestUtils.getCarUpdateRequest();
        Car car = CarTestUtils.getDefaultCar();

        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(CarTestUtils.DEFAULT_ID);
        doReturn(true)
                .when(carRepository)
                .existsByNumber(request.getNumber());

        assertThrows(BadRequestException.class, () -> carService.update(car.getId(), request));

        verify(carRepository, times(1)).findById(car.getId());
    }

    @Test
    void updateCar_WhenDriverNotExists() {
        CarCreateUpdateRequest request = CarTestUtils.getCarUpdateRequest();
        Car car = CarTestUtils.getDefaultCar();

        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(CarTestUtils.DEFAULT_ID);
        doReturn(false)
                .when(driverRepository)
                .existsById(car.getDriver().getId());

        assertThrows(BadRequestException.class, () -> carService.update(car.getId(), request));

        verify(carRepository, times(1)).findById(car.getId());
        verify(driverRepository, times(1)).existsById(car.getDriver().getId());
    }

    @Test
    void deleteExistingCar() {
        Car car = CarTestUtils.getDefaultCar();
        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(car.getId());

        carService.delete(car.getId());

        verify(carRepository, times(1)).findById(car.getId());
        verify(carRepository, times(1)).deleteById(car.getId());
    }

    @Test
    void deleteNotExistingCar() {
        Long id = CarTestUtils.DEFAULT_ID;

        assertThrows(NotFoundException.class, () -> carService.delete(id));

        verify(carRepository).findById(id);
    }

    @Test
    void getCarById_WhenCarExists() {
        Car car = CarTestUtils.getDefaultCar();
        CarResponse expected = CarTestUtils.getDefaultCarResponse();

        doReturn(Optional.of(car))
                .when(carRepository)
                .findById(car.getId());
        doReturn(expected)
                .when(carMapper)
                .mapToDto(car);

        CarResponse actual = carService.getById(car.getId());

        assertThat(actual).isEqualTo(expected);
        verify(carRepository).findById(car.getId());
        verify(carMapper).mapToDto(car);
    }

    @Test
    void getCarById_WhenCarNotFound() {
        doReturn(Optional.empty())
                .when(carRepository)
                .findById(CarTestUtils.NOT_FOUND_ID);

        assertThrows(NotFoundException.class, () -> carService.getById(CarTestUtils.NOT_FOUND_ID));

        verify(carRepository).findById(CarTestUtils.NOT_FOUND_ID);
    }

    @Test
    void getCarByDriverId() {
        Car car = CarTestUtils.getDefaultCar();
        CarResponse expected = CarTestUtils.getDefaultCarResponse();

        doReturn(Optional.of(car))
                .when(carRepository)
                .getCarByDriverId(car.getDriver().getId());
        doReturn(expected)
                .when(carMapper)
                .mapToDto(car);

        CarResponse actual = carService.getByDriverId(car.getDriver().getId());

        assertThat(actual).isEqualTo(expected);
        verify(carRepository).getCarByDriverId(car.getDriver().getId());
        verify(carMapper).mapToDto(car);
    }

    @Test
    void findAllCarsWhenParamsInvalid() {
        assertThrows(
                BadRequestException.class,
                () -> carService.getAll(CarTestUtils.INVALID_PAGE, CarTestUtils.INVALID_SIZE, CarTestUtils.INVALID_ORDER_BY)
        );
    }
}
