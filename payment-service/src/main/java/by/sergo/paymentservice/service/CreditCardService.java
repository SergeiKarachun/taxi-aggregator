package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdateDto;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponseDto;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.domain.enums.UserType;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.service.exception.ExceptionMessageUtil;
import by.sergo.paymentservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static by.sergo.paymentservice.domain.enums.Operation.WITHDRAWAL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final AccountRepository accountRepository;
    private final TransactionStoreRepository transactionStoreRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CreditCardResponseDto addCard(CreditCardCreateUpdateDto dto) {
        checkUniqueCreditCard(dto);
        checkExpirationDate(dto);
        var creditCard = creditCardRepository.save(mapToEntity(dto));
        return mapToDto(creditCard);
    }

    @Transactional
    public CreditCardResponseDto changeCard(CreditCardCreateUpdateDto dto, Long id) {
        checkCardIsUniqueForUpdate(dto, id);

        var creditCard = mapToEntity(dto);
        creditCard.setId(id);
        return mapToDto(creditCardRepository.save(creditCard));
    }

    @Transactional
    public void deleteById(Long id) {
        if (creditCardRepository.existsById(id)) {
            creditCardRepository.deleteById(id);
        }
    }

    public CreditCardResponseDto getDriverCard(Long driverId) {
        var creditCard = creditCardRepository.findByUserIdAndAndUserType(driverId, UserType.DRIVER)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessageUtil.getNotFoundMessage("Credit card with type driver", "userId", driverId)
                ));
        return mapToDto(creditCard);
    }

    public CreditCardResponseDto getPassengerCard(Long passengerId) {
        CreditCard creditCard = getPassengerCreditCard(passengerId);
        return mapToDto(creditCard);
    }

    @Transactional
    public CreditCardResponseDto makePayment(Long driverId, Long passengerId, BigDecimal sum) {
        var passengerCreditCard = getPassengerCreditCard(passengerId);
        var driverAccount = accountRepository.findByDriverId(driverId).orElseThrow(() -> new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Account", "driverId", driverId)));
        if (passengerCreditCard.getBalance().compareTo(sum) >= 0) {
            passengerCreditCard.setBalance(passengerCreditCard.getBalance().subtract(sum));
        } else {
            throw new BadRequestException(
                    ExceptionMessageUtil
                            .getWithdrawalExceptionMessage("Credit card", "passengerId", passengerId.toString(), passengerCreditCard.getBalance().toString()));
        }
        var savedCreditCard = creditCardRepository.save(passengerCreditCard);
        driverAccount.setBalance(driverAccount.getBalance().add(sum));
        accountRepository.save(driverAccount);
        var transaction = TransactionStore.builder()
                .accountNumber(driverAccount.getAccountNumber())
                .creditCardNumber(savedCreditCard.getCreditCardNumber())
                .value(sum)
                .operationDate(LocalDateTime.now())
                .operation(WITHDRAWAL)
                .build();
        transactionStoreRepository.save(transaction);
        return mapToDto(savedCreditCard);
    }

    private CreditCard getByIdOrElseThrow(Long id) {
        return creditCardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessageUtil.getNotFoundMessage("Credit card", "id", id)
                ));
    }

    private CreditCard getPassengerCreditCard(Long passengerId) {
        var creditCard = creditCardRepository.findByUserIdAndAndUserType(passengerId, UserType.PASSENGER)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessageUtil.getNotFoundMessage("Credit card with type passenger", "userId", passengerId)
                ));
        return creditCard;
    }

    private void checkCardIsUniqueForUpdate(CreditCardCreateUpdateDto dto, Long id) {
        getByIdOrElseThrow(id);
        checkUniqueCreditCard(dto);
        if (dto.getExpDate().isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    ExceptionMessageUtil
                            .getExpirationCardExceptionMessage("Credit card", "expiration date", dto.getExpDate().toString()));
        }
    }

    private static void checkExpirationDate(CreditCardCreateUpdateDto dto) {
        if (dto.getExpDate().isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    ExceptionMessageUtil
                            .getExpirationCardExceptionMessage("Credit card", "expiration date", dto.getExpDate().toString()));
        }
    }

    private void checkUniqueCreditCard(CreditCardCreateUpdateDto dto) {
        if (creditCardRepository.existsByCreditCardNumber(dto.getCreditCardNumber())) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "card number", dto.getCreditCardNumber()));
        }
    }

    public CreditCardResponseDto mapToDto(CreditCard creditCard) {
        return modelMapper.map(creditCard, CreditCardResponseDto.class);
    }

    public CreditCard mapToEntity(CreditCardCreateUpdateDto dto) {
        return modelMapper.map(dto, CreditCard.class);
    }
}
