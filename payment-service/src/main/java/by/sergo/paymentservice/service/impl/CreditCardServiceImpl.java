package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.request.PaymentRequest;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.domain.enums.UserType;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.CreditCardService;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.service.exception.ExceptionMessageUtil;
import by.sergo.paymentservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static by.sergo.paymentservice.domain.enums.Operation.PAYMENT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditCardServiceImpl implements CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final AccountRepository accountRepository;
    private final TransactionStoreRepository transactionStoreRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CreditCardResponse addCard(CreditCardCreateUpdate dto) {
        checkUniqueCreditCard(dto);
        checkExpirationDate(dto);
        var entity = mapToEntity(dto);
        entity.setId(null);
        var creditCard = creditCardRepository.save(entity);
        creditCardRepository.flush();
        return mapToDto(creditCard);
    }

    @Override
    @Transactional
    public CreditCardResponse changeCard(CreditCardCreateUpdate dto, Long id) {
        checkCardIsUniqueForUpdate(dto, id);
        var creditCard = mapToEntity(dto);
        creditCard.setId(id);
        return mapToDto(creditCardRepository.save(creditCard));
    }

    @Override
    @Transactional
    public CreditCardResponse deleteById(Long id) {
        var creditCard = getByIdOrElseThrow(id);
        creditCardRepository.deleteById(id);
        return mapToDto(creditCard);
    }

    @Override
    public CreditCardResponse getDriverCard(Long driverId) {
        var creditCard = creditCardRepository.findByUserIdAndUserType(driverId, UserType.DRIVER)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card with type driver", "userId", driverId)));
        return mapToDto(creditCard);
    }

    @Override
    public CreditCardResponse getPassengerCard(Long passengerId) {
        CreditCard creditCard = getPassengerCreditCard(passengerId);
        return mapToDto(creditCard);
    }

    @Override
    @Transactional
    public CreditCardResponse makePayment(PaymentRequest payment) {
        var passengerCreditCard = getPassengerCreditCard(payment.getPassengerId());
        var driverAccount = accountRepository.findByDriverId(payment.getDriverId())
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Account", "driverId", payment.getDriverId())));
        if (passengerCreditCard.getBalance().compareTo(payment.getSum()) >= 0) {
            passengerCreditCard.setBalance(passengerCreditCard.getBalance().subtract(payment.getSum()));
        } else {
            throw new BadRequestException(ExceptionMessageUtil.getWithdrawalExceptionMessage("Credit card", "passengerId", payment.getPassengerId().toString(), passengerCreditCard.getBalance().toString()));
        }
        var savedCreditCard = creditCardRepository.save(passengerCreditCard);
        driverAccount.setBalance(driverAccount.getBalance().add(payment.getSum()));
        accountRepository.save(driverAccount);
        var transaction = TransactionStore.builder()
                .accountNumber(driverAccount.getAccountNumber())
                .creditCardNumber(savedCreditCard.getCreditCardNumber())
                .value(payment.getSum())
                .operationDate(LocalDateTime.now())
                .operation(PAYMENT)
                .build();
        transactionStoreRepository.save(transaction);
        return mapToDto(savedCreditCard);
    }

    private CreditCard getByIdOrElseThrow(Long id) {
        return creditCardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card", "id", id)));
    }

    private CreditCard getPassengerCreditCard(Long passengerId) {
        return creditCardRepository.findByUserIdAndUserType(passengerId, UserType.PASSENGER)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card with type passenger", "userId", passengerId)));
    }

    private void checkCardIsUniqueForUpdate(CreditCardCreateUpdate dto, Long id) {
        getByIdOrElseThrow(id);
        checkUniqueCreditCard(dto);
        if (creditCardRepository.existsByUserIdAndUserType(dto.getUserId(), UserType.valueOf(dto.getUserType()))){
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "userId", dto.getUserId(), "userType", dto.getUserType()));
        }
        if (dto.getExpDate().isBefore(LocalDate.now())) {
            throw new BadRequestException(ExceptionMessageUtil.getExpirationCardExceptionMessage("Credit card", "expiration date", dto.getExpDate().toString()));
        }
    }

    private static void checkExpirationDate(CreditCardCreateUpdate dto) {
        if (dto.getExpDate().isBefore(LocalDate.now())) {
            throw new BadRequestException(ExceptionMessageUtil.getExpirationCardExceptionMessage("Credit card", "expiration date", dto.getExpDate().toString()));
        }
    }

    private void checkUniqueCreditCard(CreditCardCreateUpdate dto) {
        if (creditCardRepository.existsByCreditCardNumber(dto.getCreditCardNumber())) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "card number", dto.getCreditCardNumber()));
        }
    }

    public CreditCardResponse mapToDto(CreditCard creditCard) {
        return modelMapper.map(creditCard, CreditCardResponse.class);
    }

    public CreditCard mapToEntity(CreditCardCreateUpdate dto) {
        return modelMapper.map(dto, CreditCard.class);
    }
}
