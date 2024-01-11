package by.sergo.passengerservice.service;

import by.sergo.passengerservice.domain.dto.PassengerCreateUpdateRequestDto;
import by.sergo.passengerservice.domain.dto.PassengerListResponseDto;
import by.sergo.passengerservice.domain.dto.PassengerResponseDto;
import by.sergo.passengerservice.domain.entity.Passenger;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.service.exception.BadRequestException;
import by.sergo.passengerservice.service.exception.ExceptionMessageUtil;
import by.sergo.passengerservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerService {

    private final ModelMapper modelMapper;
    private final PassengerRepository passengerRepository;

    @Transactional
    public PassengerResponseDto create(PassengerCreateUpdateRequestDto dto) {
        checkIsPassengerUnique(dto);
        var savedPassenger = passengerRepository.save(mapToEntity(dto));
        return mapToDto(savedPassenger);
    }

    @Transactional
    public PassengerResponseDto update(Long id, PassengerCreateUpdateRequestDto dto) {
        var passengerToUpdate = getByIdOrElseThrow(id);

        checkIsPassengerForUpdateUnique(dto, passengerToUpdate);

        var passenger = mapToEntity(dto);
        passenger.setId(passengerToUpdate.getId());
        return mapToDto(passengerRepository.save(passenger));
    }

    @Transactional
    public boolean delete(Long id) {
        if (passengerRepository.existsById(id)) {
            passengerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public PassengerResponseDto getById(Long id) {
        var passenger = getByIdOrElseThrow(id);
        return mapToDto(passenger);
    }

    public PassengerResponseDto getByPhone(String phone) {
        var passenger = passengerRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessageUtil.getNotFoundMessage("Passenger", "phone", phone)
                ));
        return mapToDto(passenger);
    }

    public PassengerListResponseDto getAll() {
        List<PassengerResponseDto> passengers = passengerRepository.findAll().stream()
                .map(entity -> mapToDto(entity))
                .collect(Collectors.toList());

        return PassengerListResponseDto.builder()
                .passengers(passengers)
                .size(passengers.size())
                .total(passengers.size())
                .build();
    }

    public PassengerListResponseDto getAll(Integer page, Integer size, String field) {
        var responsePage = passengerRepository.findAll(PageRequest.of(page, size).withSort(Sort.by(field)))
                .map(this::mapToDto);
        return PassengerListResponseDto.builder()
                .passengers(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(field)
                .build();
    }

    public PassengerListResponseDto getAll(Integer page, Integer size) {
        var responsePage = passengerRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToDto);
        return PassengerListResponseDto.builder()
                .passengers(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .build();
    }

    private void checkIsPassengerForUpdateUnique(PassengerCreateUpdateRequestDto dto, Passenger entity) {
        if (!Objects.equals(dto.getEmail(), entity.getEmail())) {
            checkEmailIsUnique(dto);
        }

        if (!Objects.equals(dto.getPhone(), entity.getPhone())) {
            checkPhoneIsUnique(dto);
        }
    }

    private Passenger getByIdOrElseThrow(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessageUtil.getNotFoundMessage("Passenger", "id", id)
                ));
    }

    private void checkIsPassengerUnique(PassengerCreateUpdateRequestDto dto) {
        checkEmailIsUnique(dto);
        checkPhoneIsUnique(dto);
    }

    private void checkPhoneIsUnique(PassengerCreateUpdateRequestDto dto) {
        if (passengerRepository.existsByPhone(dto.getPhone())) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getAlreadyExistMessage("Passenger", "phone", dto.getPhone()));
        }
    }

    private void checkEmailIsUnique(PassengerCreateUpdateRequestDto dto) {
        if (passengerRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getAlreadyExistMessage("Passenger", "email", dto.getEmail()));
        }
    }


    private Passenger mapToEntity(PassengerCreateUpdateRequestDto passengerResponseDto) {
        return modelMapper.map(passengerRepository, Passenger.class);
    }

    private PassengerResponseDto mapToDto(Passenger passenger) {
        return modelMapper.map(passenger, PassengerResponseDto.class);
    }

}
