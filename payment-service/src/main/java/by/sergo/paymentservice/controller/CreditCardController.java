package by.sergo.paymentservice.controller;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdateDto;
import by.sergo.paymentservice.domain.dto.request.PaymentRequestDto;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponseDto;
import by.sergo.paymentservice.domain.dto.response.StringResponse;
import by.sergo.paymentservice.service.CreditCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/creditcards")
@RequiredArgsConstructor
public class CreditCardController {
    private final CreditCardService creditCardService;

    @PostMapping
    public ResponseEntity<CreditCardResponseDto> addCard(@RequestBody @Valid CreditCardCreateUpdateDto dto) {
        return ResponseEntity.ok(creditCardService.addCard(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditCardResponseDto> changeCard(@PathVariable("id") Long id,
                                                            @RequestBody @Valid CreditCardCreateUpdateDto dto) {
        return ResponseEntity.ok(creditCardService.changeCard(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StringResponse> deleteBard(@PathVariable("id") Long id) {
        creditCardService.deleteById(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("Credit card successfully deleted.")
                .build());
    }

    @GetMapping("/passenger/{id}")
    public ResponseEntity<CreditCardResponseDto> getPassengerCard(@PathVariable("id") Long passengerId) {
        return ResponseEntity.ok(creditCardService.getPassengerCard(passengerId));
    }

    @GetMapping("/driver/{id}")
    public ResponseEntity<CreditCardResponseDto> getDriverCard(@PathVariable("id") Long driverId) {
        return ResponseEntity.ok(creditCardService.getDriverCard(driverId));
    }

    @PostMapping("/payment")
    public ResponseEntity<CreditCardResponseDto> makePayment(@RequestBody @Valid PaymentRequestDto dto) {
        return ResponseEntity.ok(creditCardService.makePayment(dto));
    }
}
