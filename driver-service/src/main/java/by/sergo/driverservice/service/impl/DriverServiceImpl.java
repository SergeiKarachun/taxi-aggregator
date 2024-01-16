package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.DriverListResponse;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.domain.enums.Status;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.service.DriverService;
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
public class DriverServiceImpl implements DriverService {
    private final ModelMapper modelMapper;
    private final DriverRepository driverRepository;
    private final static String CAN_NOT_CREATE_DRIVER = "Can't create new driver, please check input parameters";

    @Override
    @Transactional
    public DriverResponse create(DriverCreateUpdateRequest dto) {
        checkIsDriverUnique(dto);
        return Optional.of(mapToEntity(dto))
                .map(driverRepository::save)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(CAN_NOT_CREATE_DRIVER));
    }

    @Override
    @Transactional
    public DriverResponse update(Long id, DriverCreateUpdateRequest dto) {
        var existDriver = getByIdOrElseThrow(id);
        checkIsDriverForUpdateUnique(dto, existDriver);
        var driverToSave = mapToEntity(dto);
        driverToSave.setId(id);
        return mapToDto(driverRepository.save(existDriver));
    }

    @Override
    @Transactional
    public DriverResponse delete(Long id) {
        var driver = getByIdOrElseThrow(id);
        driverRepository.deleteById(id);
        return mapToDto(driver);
    }

    @Override
    public DriverResponse getById(Long id) {
        var driver = getByIdOrElseThrow(id);
        return mapToDto(driver);
    }

    @Override
    public DriverListResponse getAvailableDrivers(Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        var responsePage = driverRepository.getAllByStatus(Status.AVAILABLE, pageRequest)
                .map(this::mapToDto);
        return DriverListResponse.builder()
                .drivers(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    @Override
    public DriverListResponse getAll(Integer page, Integer size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        var responsePage = driverRepository.findAll(pageRequest)
                .map(this::mapToDto);
        return DriverListResponse.builder()
                .drivers(responsePage.getContent())
                .page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField(orderBy)
                .build();
    }

    @Override
    @Transactional
    public DriverResponse changeStatus(Long id) {
        var driver = getByIdOrElseThrow(id);
        if (driver.getStatus().equals(Status.AVAILABLE)) {
            driver.setStatus(Status.UNAVAILABLE);
        } else driver.setStatus(Status.AVAILABLE);
        return mapToDto(driverRepository.save(driver));
    }

    private PageRequest getPageRequest(Integer page, Integer size, String orderBy) {
        if (page < 1 || size < 1) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getInvalidRequestMessage(page, size));
        } else if (orderBy != null) {
            Arrays.stream(DriverResponse.class.getDeclaredFields())
                    .map(Field::getName)
                    .filter(s -> s.contains(orderBy.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getInvalidSortingParamRequestMessage(orderBy)));
            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc(orderBy.toLowerCase())));
        } else {
            return PageRequest.of(page - 1, size);
        }
    }

    private void checkIsDriverForUpdateUnique(DriverCreateUpdateRequest dto, Driver existDriver) {
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

    private void checkIsDriverUnique(DriverCreateUpdateRequest dto) {
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

    private Driver mapToEntity(DriverCreateUpdateRequest requestDto) {
        return modelMapper.map(requestDto, Driver.class);
    }

    private DriverResponse mapToDto(Driver driver) {
        return modelMapper.map(driver, DriverResponse.class);
    }
}
