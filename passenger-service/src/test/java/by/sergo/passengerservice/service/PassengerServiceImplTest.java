package by.sergo.passengerservice.service;

import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerListResponse;
import by.sergo.passengerservice.domain.dto.response.PassengerResponse;
import by.sergo.passengerservice.domain.entity.Passenger;
import by.sergo.passengerservice.mapper.PassengerMapper;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.service.exception.BadRequestException;
import by.sergo.passengerservice.service.exception.NotFoundException;
import by.sergo.passengerservice.service.impl.PassengerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static by.sergo.passengerservice.util.PassengerTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Test
    void findExistingPassengerById() {
        Passenger existPassenger = getDefaultPassenger();
        PassengerResponse expectedResponse = getDefaultPassengerResponse();

        doReturn(Optional.of(existPassenger))
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        doReturn(expectedResponse)
                .when(passengerMapper)
                .mapToDto(existPassenger);

        PassengerResponse actual = passengerService.getById(DEFAULT_ID);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(passengerRepository).findById(DEFAULT_ID);
        verify(passengerMapper).mapToDto(existPassenger);

    }

    @Test
    void findNotExistingPassengerById() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(NOT_FOUND_ID);

        assertThrows(NotFoundException.class,
                () -> passengerService.getById(NOT_FOUND_ID));

        verify(passengerRepository)
                .findById(NOT_FOUND_ID);
    }

    @Test
    void deleteExistingPassengerById() {
        Passenger passenger = getDefaultPassenger();
        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(DEFAULT_ID);

        passengerService.delete(DEFAULT_ID);

        verify(passengerRepository, times(1)).findById(DEFAULT_ID);
        verify(passengerRepository, times(1)).deleteById(DEFAULT_ID);
    }

    @Test
    void deleteNotExistingPassengerById() {
        Long id = DEFAULT_ID;

        assertThrows(NotFoundException.class, () -> passengerService.delete(id));

        verify(passengerRepository).findById(id);
    }

    @Test
    void saveUniquePassenger() {
        PassengerResponse expected = getDefaultPassengerResponse();
        Passenger passengerToSave = getPassengerToSave();
        Passenger savedPassenger = getDefaultPassenger();
        PassengerCreateUpdateRequest createRequest = getPassengerRequest();

        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(DEFAULT_EMAIL);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(DEFAULT_PHONE);
        doReturn(passengerToSave)
                .when(passengerMapper)
                .mapToEntity(createRequest);
        doReturn(savedPassenger)
                .when(passengerRepository)
                .save(passengerToSave);
        doReturn(expected)
                .when(passengerMapper)
                .mapToDto(savedPassenger);
        PassengerResponse actual = passengerService.create(createRequest);

        verify(passengerRepository).existsByEmail(DEFAULT_EMAIL);
        verify(passengerRepository).existsByPhone(DEFAULT_PHONE);
        verify(passengerRepository).save(passengerToSave);
        verify(passengerMapper).mapToEntity(createRequest);
        verify(passengerMapper).mapToDto(savedPassenger);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void savePassengerWhenPhoneNotUnique() {
        PassengerCreateUpdateRequest request = getPassengerRequest();
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(any());

        assertThrows(BadRequestException.class, () -> passengerService.create(request));

        verify(passengerRepository, times(1)).existsByPhone(any());
    }

    @Test
    void savePassengerWhenEmailNotUnique() {
        PassengerCreateUpdateRequest request = getPassengerRequest();
        doReturn(true)
                .when(passengerRepository)
                .existsByEmail(any());

        assertThrows(BadRequestException.class, () -> passengerService.create(request));

        verify(passengerRepository, times(1)).existsByEmail(any());
    }

    @Test
    void updatePassengerWhenPassengerNotExists() {
        PassengerCreateUpdateRequest request = getPassengerRequest();

        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(NOT_FOUND_ID);

        assertThrows(NotFoundException.class, () -> passengerService.update(NOT_FOUND_ID, request));

        verify(passengerRepository).findById(NOT_FOUND_ID);
    }

    @Test
    void updatePassengerCorrectly() {
        Passenger passenger = getDefaultPassenger();
        PassengerCreateUpdateRequest request = getPassengerUpdateRequest();
        Passenger updatePassenger = getUpdatedPassenger();

        PassengerResponse expected = getUpdatePassengerResponse();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        doReturn(updatePassenger)
                .when(passengerMapper)
                .mapToEntity(request);
        doReturn(expected)
                .when(passengerMapper)
                .mapToDto(updatePassenger);
        doReturn(updatePassenger)
                .when(passengerRepository)
                .save(updatePassenger);

        PassengerResponse updated = passengerService.update(DEFAULT_ID, request);

        assertEquals(expected, updated);
        verify(passengerRepository).save(any());
    }

    @Test
    void updatePassengerWhenPhoneNumberExists() {
        Passenger passenger = getDefaultPassenger();
        PassengerCreateUpdateRequest request = getPassengerUpdateRequest();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(request.getPhone());

        assertThrows(BadRequestException.class, () -> passengerService.update(DEFAULT_ID, request));

        verify(passengerRepository).existsByPhone(request.getPhone());
    }

    @Test
    void updatePassengerWhenEmailExists() {
        Passenger passenger = getDefaultPassenger();
        PassengerCreateUpdateRequest request = getPassengerUpdateRequest();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        doReturn(true)
                .when(passengerRepository)
                .existsByEmail(request.getEmail());

        assertThrows(BadRequestException.class, () -> passengerService.update(DEFAULT_ID, request));

        verify(passengerRepository).existsByEmail(request.getEmail());
    }

    @Test
    void findAllWhenParamsInvalid() {
        assertThrows(
                BadRequestException.class,
                () -> passengerService.getAll(INVALID_PAGE, INVALID_SIZE, INVALID_ORDER_BY)
        );
    }

    @Test
    void findAllPassengersWhenParamsValid() {
        Page<Passenger> passengerPage = mock(Page.class);
        List<Passenger> passengerList = getDefaultPassengersList();

        when(passengerPage.getContent()).thenReturn(passengerList);
        when(passengerPage.getTotalElements()).thenReturn((long) passengerList.size());
        when(passengerRepository.findAll(any(PageRequest.class))).thenReturn(passengerPage);

        PassengerListResponse response = passengerService.getAll(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(passengerList.size(), passengerPage.getTotalElements());
        assertEquals(passengerList, passengerPage.getContent());
    }
}