package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequest;
import by.sergo.paymentservice.domain.dto.response.AccountResponse;

import java.math.BigDecimal;

public interface AccountService {
    AccountResponse createAccount(AccountCreateUpdateRequest dto);

    AccountResponse deleteById(Long id);

    AccountResponse withdrawalBalance(Long driverId, BigDecimal sum);

    AccountResponse getByDriverId(Long driverId);

    AccountResponse getById(Long id);
}
