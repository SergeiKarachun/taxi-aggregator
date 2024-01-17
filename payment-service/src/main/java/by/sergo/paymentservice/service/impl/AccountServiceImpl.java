package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequest;
import by.sergo.paymentservice.domain.dto.response.AccountResponse;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.mapper.AccountMapper;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.AccountService;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.util.ExceptionMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static by.sergo.paymentservice.domain.enums.Operation.WITHDRAWAL;
import static by.sergo.paymentservice.domain.enums.UserType.DRIVER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final CreditCardRepository creditCardRepository;
    private final TransactionStoreRepository transactionStoreRepository;

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateUpdateRequest dto) {
        if (accountRepository.existsByDriverId(dto.getDriverId())) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Account", "driverId", dto.getDriverId().toString()));
        }
        var account = accountMapper.mapToEntity(dto);
        account.setAccountNumber(generateAccountNumber());
        return accountMapper.mapToDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountResponse deleteById(Long id) {
        var account = getByIdOrElseThrow(id);
        accountRepository.deleteById(id);
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountResponse withdrawalBalance(Long driverId, BigDecimal sum) {
        var account = getByDriverIdOrElseThrow(driverId);
        var creditCard = getByUserIdAndUserTypeOrElseThrow(driverId);

        if (account.getBalance().compareTo(sum) >= 0) {
            account.setBalance(account.getBalance().subtract(sum));
        } else {
            throw new BadRequestException(ExceptionMessageUtil.getWithdrawalExceptionMessage("Account", "driverId", driverId.toString(), account.getBalance().toString()));
        }

        creditCard.setBalance(creditCard.getBalance().add(sum));
        creditCardRepository.save(creditCard);
        var transaction = TransactionStore.builder()
                .creditCardNumber(creditCard.getCreditCardNumber())
                .accountNumber(account.getAccountNumber())
                .value(sum)
                .operationDate(LocalDateTime.now())
                .operation(WITHDRAWAL)
                .build();
        transactionStoreRepository.save(transaction);
        return accountMapper.mapToDto(accountRepository.save(account));
    }

    @Override
    public AccountResponse getByDriverId(Long driverId) {
        return accountMapper.mapToDto(getByDriverIdOrElseThrow(driverId));
    }

    @Override
    public AccountResponse getById(Long id) {
        return accountMapper.mapToDto(getByIdOrElseThrow(id));
    }

    private CreditCard getByUserIdAndUserTypeOrElseThrow(Long driverId) {
        return creditCardRepository.findByUserIdAndUserType(driverId, DRIVER)
                .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getNotFoundMessage("CreditCard", "driverId", driverId)));
    }

    private Account getByIdOrElseThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getNotFoundMessage("Account", "id", id)));
    }

    private Account getByDriverIdOrElseThrow(Long driverId) {
        return accountRepository.findByDriverId(driverId)
                .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getNotFoundMessage("Account", "driverId", driverId)));
    }

    private String generateAccountNumber() {
        String accountNumber = UUID.randomUUID().toString().replace("-", "");
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = UUID.randomUUID().toString().replace("-", "");
        }
        return accountNumber;
    }
}
