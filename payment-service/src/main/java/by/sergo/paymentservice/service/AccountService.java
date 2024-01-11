package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequestDto;
import by.sergo.paymentservice.domain.dto.response.AccountResponseDto;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.service.exception.ExceptionMessageUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
public class AccountService {
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final CreditCardRepository creditCardRepository;
    private final TransactionStoreRepository transactionStoreRepository;

    @Transactional
    public AccountResponseDto createAccount(AccountCreateUpdateRequestDto dto) {
        if (accountRepository.existsByDriverId(dto.getDriverId())) {
            throw new BadRequestException(
                    ExceptionMessageUtil
                            .getAlreadyExistMessage("Account", "driverId", dto.getDriverId().toString()));
        }
        var account = mapToEntity(dto);
        account.setAccountNumber(generateAccountNumber());
        return mapToDto(accountRepository.save(account));
    }

    @Transactional
    public void deleteById(Long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
        }
    }

    @Transactional
    public AccountResponseDto withdrawalBalance(Long driverId, BigDecimal sum) {
        var account = getByDriverIdOrElseThrow(driverId);
        var creditCard = getByUserIdAndUserTypeOrElseThrow(driverId);
        if (account.getBalance().compareTo(sum) >= 0) {
            account.setBalance(account.getBalance().subtract(sum));
        } else {
            throw new BadRequestException(
                    ExceptionMessageUtil
                            .getWithdrawalExceptionMessage("Account", "driverId", driverId.toString(), account.getBalance().toString()));
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
        return mapToDto(accountRepository.save(account));
    }

    public AccountResponseDto getByDriverId(Long driverId) {
        return mapToDto(getByDriverIdOrElseThrow(driverId));
    }

    public AccountResponseDto getById(Long id) {
        return mapToDto(getByIdOrElseThrow(id));
    }

    private CreditCard getByUserIdAndUserTypeOrElseThrow(Long driverId) {
        return creditCardRepository.findByUserIdAndAndUserType(driverId, DRIVER)
                .orElseThrow(() -> new BadRequestException(
                        ExceptionMessageUtil.getNotFoundMessage("CreditCard", "driverId", driverId)));
    }

    private Account getByIdOrElseThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        ExceptionMessageUtil.getNotFoundMessage("Account", "id", id)));
    }

    private Account getByDriverIdOrElseThrow(Long driverId) {
        return accountRepository.findByDriverId(driverId)
                .orElseThrow(() -> new BadRequestException(
                        ExceptionMessageUtil.getNotFoundMessage("Account", "driverId", driverId)));
    }

    private Account mapToEntity(AccountCreateUpdateRequestDto dto) {
        return modelMapper.map(dto, Account.class);
    }

    private AccountResponseDto mapToDto(Account account) {
        return modelMapper.map(account, AccountResponseDto.class);
    }

    private String generateAccountNumber() {
        String accountNumber = UUID.randomUUID().toString().replace("-", "");
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = UUID.randomUUID().toString().replace("-", "");
        }
        return accountNumber;
    }
}
