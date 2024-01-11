package by.sergo.passengerservice.controller;

import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequestDto;
import by.sergo.passengerservice.domain.dto.response.PassengerListResponseDto;
import by.sergo.passengerservice.domain.dto.response.PassengerResponseDto;
import by.sergo.passengerservice.domain.dto.response.StringResponse;
import by.sergo.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PassengerResponseDto> create(@RequestBody PassengerCreateUpdateRequestDto dto) {
        var passengerResponseDto = passengerService.create(dto);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PassengerResponseDto> update(@RequestBody PassengerCreateUpdateRequestDto dto,
                                                       @PathVariable("id") Long id) {
        var passengerResponseDto = passengerService.update(id, dto);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> getById(@PathVariable("id") Long id) {
        var passengerResponseDto = passengerService.getById(id);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<StringResponse> deleteById(@PathVariable("id") Long id) {
        passengerService.delete(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("Passenger with id" + id + " has been deleted")
                .build());
    }

    @GetMapping
    public ResponseEntity<PassengerListResponseDto> getAll(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                           @RequestParam(required = false, defaultValue = "10") Integer size,
                                                           @RequestParam(required = false) String orderBy) {
        var passengerListResponseDto = passengerService.getAll(page, size, orderBy);
        return ResponseEntity.ok(passengerListResponseDto);
    }
}
