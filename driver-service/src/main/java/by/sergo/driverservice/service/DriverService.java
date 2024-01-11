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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                .orElseThrow(() -> new BadRequestException(HttpStatus.BAD_REQUEST, "Can't create new driver, please check input parameters"));
    }

    @Transactional
    public DriverResponseDto update(Long id, DriverCreateUpdateRequestDto dto) {
        var existDriver = getByIdOrElseThrow(id);

        checkIsDriverForUpdateUnique(dto, existDriver);

        var driverToSave = mapToEntity(dto);
        driverToSave.setId(id);

        return Optional.of(driverToSave)
                .map(driverRepository::saveAndFlush)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(HttpStatus.BAD_REQUEST, "Can't update driver, please check input parameters"));
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

    @Transactional
    public DriverResponseDto editRating(Integer grade, Long id) {
        var driver = getByIdOrElseThrow(id);
        driver.setRating((driver.getRating() + grade)/2);
        return Optional.of(driverRepository.save(driver))
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(HttpStatus.BAD_REQUEST, "Can't update rating, please check input parameters"));
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
        if (!Objects.equals(dto.getEmail(), existDriver.getEmail())) {
            checkIsEmailUnique(dto.getEmail());
        }

        if (!Objects.equals(dto.getPhone(), existDriver.getPhone())) {
            checkIsPhoneUnique(dto.getPhone());
        }
    }

    private Driver getByIdOrElseThrow(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Driver", "id", id)));
    }

    private void checkIsDriverUnique(DriverCreateUpdateRequestDto dto) {
        checkIsEmailUnique(dto.getEmail());
        checkIsPhoneUnique(dto.getPhone());
    }

    private void checkIsPhoneUnique(String phone) {
        if (driverRepository.existsByPhone(phone)) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getAlreadyExistMessage("Driver", "phone", phone));
        }
    }

    private void checkIsEmailUnique(String email) {
        if (driverRepository.existsByEmail(email)) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getAlreadyExistMessage("Driver", "email", email));
        }
    }

    private Driver mapToEntity(DriverCreateUpdateRequestDto requestDto) {
        return modelMapper.map(requestDto, Driver.class);
    }

    private DriverResponseDto mapToDto(Driver driver) {
        return modelMapper.map(driver, DriverResponseDto.class);
    }
}
