package by.sergo.passengerservice.service.impl;

import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerListResponse;
import by.sergo.passengerservice.domain.dto.response.PassengerResponse;
import by.sergo.passengerservice.domain.entity.Passenger;
import by.sergo.passengerservice.mapper.PassengerMapper;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.service.PassengerService;
import by.sergo.passengerservice.service.exception.BadRequestException;
import by.sergo.passengerservice.util.ExceptionMessageUtil;
import by.sergo.passengerservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static by.sergo.passengerservice.util.ExceptionMessageUtil.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper passengerMapper;
    private final PassengerRepository passengerRepository;

    @Override
    @Transactional
    public PassengerResponse create(PassengerCreateUpdateRequest dto) {
        checkIsPassengerUnique(dto);
        var savedPassenger = passengerRepository.save(passengerMapper.mapToEntity(dto));
        return passengerMapper.mapToDto(savedPassenger);
    }

    @Override
    @Transactional
    public PassengerResponse update(Long id, PassengerCreateUpdateRequest dto) {
        var passengerToUpdate = getByIdOrElseThrow(id);

        checkIsPassengerForUpdateUnique(dto, passengerToUpdate);

        var passenger = passengerMapper.mapToEntity(dto);
        passenger.setId(passengerToUpdate.getId());
        return passengerMapper.mapToDto(passengerRepository.save(passenger));
    }

    @Override
    @Transactional
    public PassengerResponse delete(Long id) {
        var passenger = getByIdOrElseThrow(id);
        passengerRepository.deleteById(id);
        return passengerMapper.mapToDto(passenger);
    }

    @Override
    public PassengerResponse getById(Long id) {
        var passenger = getByIdOrElseThrow(id);
        return passengerMapper.mapToDto(passenger);
    }

    @Override
    public PassengerResponse getByPhone(String phone) {
        var passenger = passengerRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException(getNotFoundMessage("Passenger", "phone", phone)));
        return passengerMapper.mapToDto(passenger);
    }

    @Override
    public PassengerListResponse getAll() {
        List<PassengerResponse> passengers = passengerRepository.findAll().stream()
                .map(passengerMapper::mapToDto)
                .collect(Collectors.toList());

        return PassengerListResponse.builder()
                .passengers(passengers)
                .size(passengers.size())
                .total(passengers.size())
                .build();
    }

    @Override
    public PassengerListResponse getAll(Integer page, Integer size, String field) {
        PageRequest pageRequest = getPageRequest(page, size, field);
        Page<Passenger> responsePage = passengerRepository.findAll(pageRequest);
        List<Passenger> passengers = responsePage.getContent();
        List<PassengerResponse> passengerResponseList = passengers.stream()
                .map(passengerMapper::mapToDto)
                .toList();

        return PassengerListResponse.builder()
                .passengers(passengerResponseList)
                .totalPages(responsePage.getTotalPages())
                .size(passengers.size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(field)
                .build();
    }

    private PageRequest getPageRequest(Integer page, Integer size, String field) {
        if (page < 1 || size < 1) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidRequestMessage(page, size));
        } else if (field != null) {
            checkFieldForSort(field);
            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc(field.toLowerCase())));
        } else {
            return PageRequest.of(page - 1, size);
        }
    }

    private void checkFieldForSort(String field) {
        Arrays.stream(PassengerResponse.class.getDeclaredFields())
                .map(Field::getName)
                .filter(s -> s.contains(field.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(getInvalidSortingParamRequestMessage(field)));
    }

    private void checkIsPassengerForUpdateUnique(PassengerCreateUpdateRequest dto, Passenger entity) {
        var errors = new HashMap<String, String>();
        if (!Objects.equals(dto.getEmail(), entity.getEmail())) {
            checkEmailIsUnique(dto, errors);
        }

        if (!Objects.equals(dto.getPhone(), entity.getPhone())) {
            checkPhoneIsUnique(dto, errors);
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException(getAlreadyExistMapMessage(errors));
        }
    }

    private Passenger getByIdOrElseThrow(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(getNotFoundMessage("Passenger", "id", id)));
    }

    private void checkIsPassengerUnique(PassengerCreateUpdateRequest dto) {
        var errors = new HashMap<String, String>();

        checkEmailIsUnique(dto, errors);
        checkPhoneIsUnique(dto, errors);

        if (!errors.isEmpty()) {
            throw new BadRequestException(getAlreadyExistMapMessage(errors));
        }
    }

    private void checkPhoneIsUnique(PassengerCreateUpdateRequest dto, HashMap<String, String> errors) {
        if (passengerRepository.existsByPhone(dto.getPhone())) {
            errors.put("phone", getAlreadyExistMessage("Passenger", "phone", dto.getPhone()));
        }
    }

    private void checkEmailIsUnique(PassengerCreateUpdateRequest dto, HashMap<String, String> errors) {
        if (passengerRepository.existsByEmail(dto.getEmail())) {
            errors.put("email", getAlreadyExistMessage("Passenger", "email", dto.getEmail()));
        }
    }
}
