package by.sergo.paymentservice.controller;

import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequestDto;
import by.sergo.paymentservice.domain.dto.response.AccountResponseDto;
import by.sergo.paymentservice.domain.dto.response.StringResponse;
import by.sergo.paymentservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@RequestBody @Valid AccountCreateUpdateRequestDto dto) {
        return ResponseEntity.ok(accountService.createAccount(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StringResponse> delete(@PathVariable("id") Long id) {
        accountService.deleteById(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("Account successfully deleted.")
                .build());
    }

    @PutMapping("/driver/{id}")
    public ResponseEntity<AccountResponseDto> withdrawalBalance(@PathVariable("id") Long driverId,
                                                                @RequestParam(value = "sum", required = true) BigDecimal sum) {
        return ResponseEntity.ok(accountService.withdrawalBalance(driverId, sum));
    }

    @GetMapping("/driver/{id}")
    public ResponseEntity<AccountResponseDto> getDriverAccount(@PathVariable("id") Long id) {
        return ResponseEntity.ok(accountService.getByDriverId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(accountService.getById(id));
    }
}
