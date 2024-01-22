package by.sergo.passengerservice.controller;

import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerListResponse;
import by.sergo.passengerservice.domain.dto.response.PassengerResponse;
import by.sergo.passengerservice.service.impl.PassengerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerServiceImpl passengerServiceImpl;

    @PostMapping
    public ResponseEntity<PassengerResponse> create(@RequestBody @Valid PassengerCreateUpdateRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(passengerServiceImpl.create(dto));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<PassengerResponse> update(@RequestBody @Valid PassengerCreateUpdateRequest dto,
                                                    @PathVariable("id") Long id) {
        var passengerResponseDto = passengerServiceImpl.update(id, dto);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponse> getById(@PathVariable("id") Long id) {
        var passengerResponseDto = passengerServiceImpl.getById(id);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PassengerResponse> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                             .body(passengerServiceImpl.delete(id));
    }

    @GetMapping
    public ResponseEntity<PassengerListResponse> getAll(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                        @RequestParam(value = "orderBy", required = false) String orderBy) {
        var passengerListResponseDto = passengerServiceImpl.getAll(page, size, orderBy);
        return ResponseEntity.ok(passengerListResponseDto);
    }
}
