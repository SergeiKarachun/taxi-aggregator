package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequestDto;
import by.sergo.driverservice.domain.dto.response.DriverListResponseDto;
import by.sergo.driverservice.domain.dto.response.DriverResponseDto;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.domain.enums.Status;
import by.sergo.driverservice.repository.DriverRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {
    private final ModelMapper modelMapper;
    private final DriverRepository driverRepository;

    @Transactional
    public DriverResponseDto create(DriverCreateUpdateRequestDto dto) {
        checkIsDriverUnique(dto);
        return Optional.of(mapToEntity(dto))
                .map(driverRepository::saveAndFlush)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException("Can't create new driver, please check input parameters"));
    }

    @Transactional
    public DriverResponseDto update(Long id, DriverCreateUpdateRequestDto dto) {
        var existDriver = getByIdOrElseThrow(id);
        checkIsDriverForUpdateUnique(dto, existDriver);
        existDriver.setName(dto.getName());
        existDriver.setSurname(dto.getSurname());
        existDriver.setEmail(dto.getEmail());
        existDriver.setPhone(dto.getPhone());
        return mapToDto(driverRepository.save(existDriver));
    }

    @Transactional
    public void delete(Long id) {
        if (driverRepository.existsById(id)){
            driverRepository.deleteById(id);
        } else throw new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Driver", "id", id));
    }

    public DriverResponseDto getById(Long id) {
        var driver = getByIdOrElseThrow(id);
        return mapToDto(driver);
    }

    public DriverListResponseDto getAvailableDrivers(Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        var responsePage = driverRepository.getAllByStatus(Status.AVAILABLE, pageRequest)
                .map(this::mapToDto);
        return DriverListResponseDto.builder()
                .drivers(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    public DriverListResponseDto getAll(Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        var responsePage = driverRepository.findAll(pageRequest)
                .map(this::mapToDto);
        return DriverListResponseDto.builder()
                .drivers(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    @Transactional
    public void changeStatus(Long id) {
        var driver = getByIdOrElseThrow(id);
        if (driver.getStatus().equals(Status.AVAILABLE)) {
            driver.setStatus(Status.UNAVAILABLE);
        } else driver.setStatus(Status.AVAILABLE);
        driverRepository.save(driver);
    }

    private PageRequest getPageRequest(Integer page, Integer size, String orderBy) {
        if (page < 1 || size < 1) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getInvaLidRequestMessage(page, size));
        }

        if (orderBy != null) {
            List<String> declaredFields = Arrays.stream(Driver.class.getDeclaredFields())
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

    private void checkIsDriverForUpdateUnique(DriverCreateUpdateRequestDto dto, Driver existDriver) {
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

    private void checkIsDriverUnique(DriverCreateUpdateRequestDto dto) {
        var errors = new HashMap<String, String>();
        checkIsEmailUnique(dto.getEmail(), errors);
        checkIsPhoneUnique(dto.getPhone(), errors);

        if (!errors.isEmpty()) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMapMessage(errors));
        }
    }

    private void checkIsPhoneUnique(String phone, HashMap<String, String> errors) {
        if (driverRepository.existsByPhone(phone)) {
            errors.put("phone", ExceptionMessageUtil.getAlreadyExistMessage("Driver", "phone", phone));
        }
    }

    private void checkIsEmailUnique(String email, HashMap<String, String> errors) {
        if (driverRepository.existsByEmail(email)) {
            errors.put("email", ExceptionMessageUtil.getAlreadyExistMessage("Driver", "email", email));
        }
    }

    private Driver mapToEntity(DriverCreateUpdateRequestDto requestDto) {
        return modelMapper.map(requestDto, Driver.class);
    }

    private DriverResponseDto mapToDto(Driver driver) {
        return modelMapper.map(driver, DriverResponseDto.class);
    }
}
