package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.request.PaymentRequest;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.domain.enums.UserType;
import by.sergo.paymentservice.mapper.CreditCardMapper;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.CreditCardService;
import by.sergo.paymentservice.service.DriverService;
import by.sergo.paymentservice.service.PassengerService;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.util.ExceptionMessageUtil;
import by.sergo.paymentservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static by.sergo.paymentservice.domain.enums.Operation.PAYMENT;
import static by.sergo.paymentservice.domain.enums.UserType.DRIVER;
import static by.sergo.paymentservice.domain.enums.UserType.PASSENGER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditCardServiceImpl implements CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final AccountRepository accountRepository;
    private final TransactionStoreRepository transactionStoreRepository;
    private final CreditCardMapper creditCardMapper;
    private final PassengerService passengerService;
    private final DriverService driverService;

    @Override
    @Transactional
    public CreditCardResponse addCard(CreditCardCreateUpdate dto) {
        UserResponse userResponse = getUserResponse(dto.getUserId(), dto.getUserType());

        checkUniqueCreditCardAndExpirationDate(dto);

        var creditCard = creditCardMapper.mapToEntity(dto);
        creditCard.setId(null);
        var savedCreditCard = creditCardRepository.save(creditCard);
        return getCreditCardResponse(userResponse, savedCreditCard);
    }

    @Override
    @Transactional
    public CreditCardResponse changeCard(CreditCardCreateUpdate dto, Long id) {
        UserResponse userResponse = getUserResponse(dto.getUserId(), dto.getUserType());

        checkCardIsUniqueForUpdate(dto, id);

        var creditCard = creditCardMapper.mapToEntity(dto);
        creditCard.setId(id);
        return getCreditCardResponse(userResponse, creditCardRepository.save(creditCard));
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
        UserResponse userResponse = getUserResponse(driverId, DRIVER.name());

        var creditCard = creditCardRepository.findByUserIdAndUserType(driverId, DRIVER)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card with type driver", "userId", driverId)));
        return getCreditCardResponse(userResponse, creditCard);
    }

    @Override
    public CreditCardResponse getPassengerCard(Long passengerId) {
        UserResponse userResponse = getUserResponse(passengerId, PASSENGER.name());

        CreditCard creditCard = getPassengerCreditCard(passengerId);
        return getCreditCardResponse(userResponse, creditCard);
    }

    @Override
    @Transactional
    public CreditCardResponse makePayment(PaymentRequest payment) {
        UserResponse userResponse = getUserResponse(payment.getPassengerId(), PASSENGER.name());

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
        return getCreditCardResponse(userResponse, savedCreditCard);
    }

    private CreditCard getByIdOrElseThrow(Long id) {
        return creditCardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card", "id", id)));
    }

    private CreditCard getPassengerCreditCard(Long passengerId) {
        return creditCardRepository.findByUserIdAndUserType(passengerId, PASSENGER)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card with type passenger", "userId", passengerId)));
    }

    private void checkCardIsUniqueForUpdate(CreditCardCreateUpdate dto, Long id) {
        if (!creditCardRepository.existsById(id)) {
            throw new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card", "id", id));
        }
        if (!creditCardRepository.existsByUserIdAndUserType(dto.getUserId(), UserType.valueOf(dto.getUserType()))) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "userId", dto.getUserId(), "userType", dto.getUserType()));
        }
        if (creditCardRepository.existsByCreditCardNumber(dto.getCreditCardNumber())) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "card number", dto.getCreditCardNumber()));
        }
    }

    private void checkUniqueCreditCardAndExpirationDate(CreditCardCreateUpdate dto) {
        if (creditCardRepository.existsByUserIdAndUserType(dto.getUserId(), UserType.valueOf(dto.getUserType()))) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "userId", dto.getUserId(), "userType", dto.getUserType()));
        }
        if (creditCardRepository.existsByCreditCardNumber(dto.getCreditCardNumber())) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "card number", dto.getCreditCardNumber()));
        }
    }

    private CreditCardResponse getCreditCardResponse(UserResponse userResponse, CreditCard creditCard) {
        var response = creditCardMapper.mapToDto(creditCard);
        response.setUser(userResponse);
        return response;
    }

    private UserResponse getUserResponse(Long id, String userType) {
        if (userType.equals(PASSENGER.name())) {
            return getPassenger(id);
        }
        else {
            return getDriver(id);
        }
    }

    private UserResponse getDriver(Long id) {
        return driverService.getDriver(id);
    }

    private UserResponse getPassenger(Long id) {
        return passengerService.getPassenger(id);
    }
}
