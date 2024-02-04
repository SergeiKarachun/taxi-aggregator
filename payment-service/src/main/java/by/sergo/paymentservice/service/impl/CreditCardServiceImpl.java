package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.request.PaymentRequest;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.domain.enums.UserType;
import by.sergo.paymentservice.mapper.CreditCardMapper;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.CreditCardService;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.util.ExceptionMessageUtil;
import by.sergo.paymentservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
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
    private final CreditCardMapper creditCardMapper;

    @Override
    @Transactional
    public CreditCardResponse addCard(CreditCardCreateUpdate dto) {
        checkUniqueCreditCardAndExpirationDate(dto);
        var creditCard = creditCardMapper.mapToEntity(dto);
        creditCard.setId(null);
        var savedCreditCard = creditCardRepository.save(creditCard);
        return creditCardMapper.mapToDto(savedCreditCard);
    }

    @Override
    @Transactional
    public CreditCardResponse changeCard(CreditCardCreateUpdate dto, Long id) {
        checkCardIsUniqueForUpdate(dto, id);
        var creditCard = creditCardMapper.mapToEntity(dto);
        creditCard.setId(id);
        return creditCardMapper.mapToDto(creditCardRepository.save(creditCard));
    }

    @Override
    @Transactional
    public CreditCardResponse deleteById(Long id) {
        var creditCard = getByIdOrElseThrow(id);
        creditCardRepository.deleteById(id);
        return creditCardMapper.mapToDto(creditCard);
    }

    @Override
    public CreditCardResponse getDriverCard(Long driverId) {
        var creditCard = creditCardRepository.findByUserIdAndUserType(driverId, UserType.DRIVER)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card with type driver", "userId", driverId)));
        return creditCardMapper.mapToDto(creditCard);
    }

    @Override
    public CreditCardResponse getPassengerCard(Long passengerId) {
        CreditCard creditCard = getPassengerCreditCard(passengerId);
        return creditCardMapper.mapToDto(creditCard);
    }

    @Override
    @Transactional
    public CreditCardResponse makePayment(PaymentRequest payment) {
        var passengerCreditCard = getPassengerCreditCard(payment.getPassengerId());
        var driverAccount = accountRepository.findByDriverId(payment.getDriverId())
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Account", "driverId", payment.getDriverId())));

        if (passengerCreditCard.getBalance().compareTo(payment.getSum()) < 0) {
            throw new BadRequestException(ExceptionMessageUtil.getWithdrawalExceptionMessage("Credit card", "passengerId", payment.getPassengerId().toString(), passengerCreditCard.getBalance().toString()));
        }
        passengerCreditCard.setBalance(passengerCreditCard.getBalance().subtract(payment.getSum()));

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
        return creditCardMapper.mapToDto(savedCreditCard);
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
        if (!creditCardRepository.existsById(id)){
            throw new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card", "id", id));
        }
        checkUniqueCreditCardAndExpirationDate(dto);
    }

    private void checkUniqueCreditCardAndExpirationDate(CreditCardCreateUpdate dto) {
        if (creditCardRepository.existsByUserIdAndUserType(dto.getUserId(), UserType.valueOf(dto.getUserType()))){
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "userId", dto.getUserId(), "userType", dto.getUserType()));
        }
        if (creditCardRepository.existsByCreditCardNumber(dto.getCreditCardNumber())) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "card number", dto.getCreditCardNumber())); 
        } 
        if (dto.getExpDate().isBefore(LocalDate.now())) {     
            throw new BadRequestException(ExceptionMessageUtil.getExpirationCardExceptionMessage("Credit card", "expiration date", dto.getExpDate().toString())); }
    }
}
