package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.request.DriverForRideResponse;
import by.sergo.driverservice.domain.dto.request.FindDriverForRideRequest;
import by.sergo.driverservice.domain.dto.response.DriverListResponse;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.domain.enums.Status;
import by.sergo.driverservice.kafka.DriverProducer;
import by.sergo.driverservice.mapper.DriverMapper;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.NotFoundException;
import by.sergo.driverservice.service.impl.DriverServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static by.sergo.driverservice.domain.enums.Status.*;
import static by.sergo.driverservice.util.DriverTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;
    @Mock
    private DriverMapper driverMapper;
    @Mock
    private DriverProducer driverProducer;

    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void saveUniqueDriver() {
        DriverCreateUpdateRequest request = getDefaultDriverRequest();
        Driver notSavedDriver = getNotSavedDriver();
        Driver savedDriver = getDefaultDriver();
        DriverResponse response = getDefaultDriverResponse();

        doReturn(false)
                .when(driverRepository)
                .existsByPhone(DEFAULT_PHONE);
        doReturn(false)
                .when(driverRepository)
                .existsByEmail(DEFAULT_EMAIL);
        doReturn(notSavedDriver)
                .when(driverMapper)
                .mapToEntity(request);
        doReturn(savedDriver)
                .when(driverRepository)
                .save(notSavedDriver);
        doReturn(response)
                .when(driverMapper)
                .mapToDto(savedDriver);

        DriverResponse actual = driverService.create(request);

        verify(driverRepository).existsByEmail(DEFAULT_EMAIL);
        verify(driverRepository).existsByPhone(DEFAULT_PHONE);
        verify(driverMapper).mapToEntity(request);
        verify(driverRepository).save(notSavedDriver);
        verify(driverMapper).mapToDto(savedDriver);
        assertThat(actual).isEqualTo(response);
    }

    @Test
    void saveDriverWhenEmailNotUnique() {
        DriverCreateUpdateRequest request = getDefaultDriverRequest();

        doReturn(true)
                .when(driverRepository)
                .existsByEmail(DEFAULT_EMAIL);

        assertThrows(BadRequestException.class, () -> driverService.create(request));

        verify(driverRepository, times(1)).existsByEmail(DEFAULT_EMAIL);
    }

    @Test
    void updateDriverWhenEmailNotUnique() {
        DriverCreateUpdateRequest request = getDriverUpdateRequest();
        Driver driver = getDefaultDriver();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(DEFAULT_DRIVER_ID);
        doReturn(true)
                .when(driverRepository)
                .existsByEmail(request.getEmail());

        assertThrows(BadRequestException.class, () -> driverService.update(DEFAULT_DRIVER_ID, request));

        verify(driverRepository).findById(DEFAULT_DRIVER_ID);
        verify(driverRepository).existsByEmail(request.getEmail());
    }

    @Test
    void updateDriverCorrectly() {
        DriverCreateUpdateRequest request = getDriverUpdateRequest();
        Driver driver = getDefaultDriver();
        Driver updatedDriver = getUpdatedDriver();
        DriverResponse expected = getUpdatedDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(DEFAULT_DRIVER_ID);
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(request.getPhone());
        doReturn(false)
                .when(driverRepository)
                .existsByEmail(request.getEmail());
        doReturn(updatedDriver)
                .when(driverMapper)
                .mapToEntity(request);
        doReturn(updatedDriver)
                .when(driverRepository)
                .save(updatedDriver);
        doReturn(expected)
                .when(driverMapper)
                .mapToDto(updatedDriver);

        DriverResponse actual = driverService.update(DEFAULT_DRIVER_ID, request);

        assertEquals(expected, actual);
        verify(driverRepository).save(any());
    }

    @Test
    void updateDriverWhenDriverNotFound() {
        DriverCreateUpdateRequest request = getDefaultDriverRequest();

        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(NOT_FOUND_ID);

        assertThrows(NotFoundException.class, () -> driverService.update(NOT_FOUND_ID, request));

        verify(driverRepository).findById(NOT_FOUND_ID);
    }

    @Test
    void deleteExistingDriverById() {
        Driver driver = getDefaultDriver();
        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(DEFAULT_DRIVER_ID);

        driverService.delete(DEFAULT_DRIVER_ID);

        verify(driverRepository, times(1)).findById(DEFAULT_DRIVER_ID);
        verify(driverRepository, times(1)).deleteById(DEFAULT_DRIVER_ID);
    }

    @Test
    void deleteNotExistingDriverById() {
        assertThrows(NotFoundException.class, () -> driverService.delete(DEFAULT_DRIVER_ID));

        verify(driverRepository).findById(DEFAULT_DRIVER_ID);
    }

    @Test
    void findExistingDriver() {
        Driver driver = getDefaultDriver();
        DriverResponse expected = getDefaultDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(DEFAULT_DRIVER_ID);
        doReturn(expected)
                .when(driverMapper)
                .mapToDto(driver);

        DriverResponse actual = driverService.getById(DEFAULT_DRIVER_ID);

        assertThat(actual).isEqualTo(expected);
        verify(driverRepository).findById(DEFAULT_DRIVER_ID);
        verify(driverMapper).mapToDto(driver);
    }

    @Test
    void findNotExistingDriverById() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(NOT_FOUND_ID);

        assertThrows(NotFoundException.class, () -> driverService.getById(NOT_FOUND_ID));

        verify(driverRepository).findById(NOT_FOUND_ID);
    }

    @Test
    void findAllWhenPageInvalid() {
        assertThrows(BadRequestException.class, () -> driverService.getAll(INVALID_PAGE, INVALID_SIZE, INVALID_ORDER_BY));
    }

    @Test
    void findAllDriversWhenParamsValid() {
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(
                getDefaultDriver(),
                getSecondDriver()
        ));

        when(driverRepository.findAll(any(PageRequest.class))).thenReturn(driverPage);
        doReturn(getDefaultDriverResponse()).when(driverMapper).mapToDto(any(Driver.class));

        DriverListResponse response = driverService.getAll(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(2, response.getDrivers().size());
        assertEquals(DEFAULT_DRIVER_ID, response.getDrivers().get(0).getId());
        verify(driverRepository).findAll(any(PageRequest.class));
        verify(driverMapper, times(2)).mapToDto(any(Driver.class));
    }

    @Test
    void findAllAvailableDriversWhenParamsValid() {
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(
                getDefaultDriver(),
                getSecondDriver()
        ));

        when(driverRepository.getAllByStatus(any(Status.class), any(PageRequest.class))).thenReturn(driverPage);
        doReturn(getDefaultDriverResponse()).when(driverMapper).mapToDto(any(Driver.class));

        DriverListResponse response = driverService.getAvailableDrivers(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(2, response.getDrivers().size());
        assertEquals(DEFAULT_DRIVER_ID, response.getDrivers().get(0).getId());
        verify(driverRepository).getAllByStatus(any(Status.class), any(PageRequest.class));
        verify(driverMapper, times(2)).mapToDto(any(Driver.class));
    }

    @Test
    void changeStatusWhenDriverNotFound() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(DEFAULT_DRIVER_ID);

        assertThrows(NotFoundException.class, () -> driverService.changeStatus(DEFAULT_DRIVER_ID));

        verify(driverRepository, times(1)).findById(DEFAULT_DRIVER_ID);
    }

    @Test
    void changeStatusWhenDriverExistsAndStatusAvailable() {
        Driver driver = getDefaultDriver();
        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(DEFAULT_DRIVER_ID);

        driverService.changeStatus(DEFAULT_DRIVER_ID);

        verify(driverRepository).findById(DEFAULT_DRIVER_ID);
        verify(driverRepository).save(driver);
        assertEquals(UNAVAILABLE, driver.getStatus());
    }

    @Test
    void changeStatusWhenDriverExistsAndStatusUnavailable() {
        Driver driver = getDefaultDriver();
        driver.setStatus(UNAVAILABLE);
        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(DEFAULT_DRIVER_ID);

        driverService.changeStatus(DEFAULT_DRIVER_ID);

        verify(driverRepository).findById(DEFAULT_DRIVER_ID);
        verify(driverProducer, times(1)).sendMessage(any(DriverForRideResponse.class));
        verify(driverRepository).save(driver);
        assertEquals(AVAILABLE, driver.getStatus());
    }

    @Test
    void findDriverForRide() {
        FindDriverForRideRequest request = gerDefaultFindDriverForRideRequest();
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(
                getDefaultDriver(),
                getSecondDriver()
        ));

        when(driverRepository.getAllByStatus(any(Status.class), any(PageRequest.class))).thenReturn(driverPage);
        doReturn(getDefaultDriverResponse()).when(driverMapper).mapToDto(any(Driver.class));

        driverService.findDriverForRide(request);

        verify(driverRepository).getAllByStatus(any(Status.class), any(PageRequest.class));
        verify(driverMapper, times(2)).mapToDto(any(Driver.class));
        verify(driverProducer).sendMessage(any(DriverForRideResponse.class));
    }

    @Test
    void findDriverForRideWhenDriversNotAvailable() {
        FindDriverForRideRequest request = gerDefaultFindDriverForRideRequest();

        when(driverRepository.getAllByStatus(any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        driverService.findDriverForRide(request);

        verify(driverRepository).getAllByStatus(any(Status.class), any(PageRequest.class));
        verify(driverMapper, never()).mapToDto(any(Driver.class));
        verify(driverProducer, never()).sendMessage(any(DriverForRideResponse.class));
    }
}