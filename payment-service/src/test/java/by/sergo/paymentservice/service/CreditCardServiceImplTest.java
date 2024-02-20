package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.request.PaymentRequest;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.mapper.CreditCardMapper;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.service.exception.NotFoundException;
import by.sergo.paymentservice.service.impl.CreditCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static by.sergo.paymentservice.domain.enums.UserType.*;
import static by.sergo.paymentservice.util.CreditCardTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreditCardServiceImplTest {

    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionStoreRepository transactionStoreRepository;
    @Mock
    private CreditCardMapper creditCardMapper;
    @Mock
    private PassengerService passengerService;
    @Mock
    private DriverService driverService;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    @Test
    void addCreditCard() {
        CreditCardCreateUpdate request = getCreditCardRequest();
        CreditCard creditCard = getCreditCard();
        CreditCardResponse response = getCreditCardResponse();

        doReturn(creditCard)
                .when(creditCardMapper)
                .mapToEntity(request);
        doReturn(creditCard)
                .when(creditCardRepository)
                .save(creditCard);
        doReturn(response)
                .when(creditCardMapper)
                .mapToDto(creditCard);

        CreditCardResponse actual = creditCardService.addCard(request);

        assertEquals(response, actual);
        verify(creditCardRepository, times(1)).save(creditCard);
        verify(creditCardRepository, times(1)).existsByUserIdAndUserType(request.getUserId(), valueOf(request.getUserType()));
        verify(creditCardRepository, times(1)).existsByCreditCardNumber(request.getCreditCardNumber());
        verify(driverService, times(1)).getDriver(any());
    }

    @Test
    void addNotUniqueCreditCard() {
        CreditCardCreateUpdate request = getCreditCardRequest();

        doReturn(true)
                .when(creditCardRepository)
                .existsByCreditCardNumber(request.getCreditCardNumber());

        assertThrows(BadRequestException.class, () -> creditCardService.addCard(request));
    }

    @Test
    void updateCreditCardSuccessfully() {
        CreditCard creditCard = getCreditCard();
        CreditCardCreateUpdate request = getCreditCardUpdateRequest();
        CreditCard updatedCreditCard = getUpdatedCreditCard();
        CreditCardResponse response = getUpdatedCreditCardResponse();

        doReturn(true)
                .when(creditCardRepository)
                .existsById(creditCard.getId());
        doReturn(true)
                .when(creditCardRepository)
                .existsByUserIdAndUserType(creditCard.getUserId(), DRIVER);
        doReturn(updatedCreditCard)
                .when(creditCardMapper)
                .mapToEntity(request);
        doReturn(updatedCreditCard)
                .when(creditCardRepository)
                .save(updatedCreditCard);
        doReturn(response)
                .when(creditCardMapper)
                .mapToDto(updatedCreditCard);

        CreditCardResponse actual = creditCardService.changeCard(request, DEFAULT_CREDIT_CARD_ID);

        assertEquals(response, actual);
        verify(creditCardRepository, times(1)).existsById(DEFAULT_CREDIT_CARD_ID);
        verify(creditCardRepository, times(1)).existsByUserIdAndUserType(request.getUserId(), valueOf(request.getUserType()));
        verify(creditCardRepository, times(1)).existsByCreditCardNumber(request.getCreditCardNumber());
        verify(driverService, times(1)).getDriver(any());
    }

    @Test
    void updateNotExistingCreditCard() {
        CreditCardCreateUpdate request = getCreditCardUpdateRequest();

        assertThrows(NotFoundException.class, () -> creditCardService.changeCard(request, DEFAULT_CREDIT_CARD_ID));

        verify(creditCardRepository, times(1)).existsById(DEFAULT_CREDIT_CARD_ID);
        verify(creditCardRepository, never()).existsByCreditCardNumber(request.getCreditCardNumber());
    }

    @Test
    void deleteExistingCreditCard() {
        CreditCard creditCard = getCreditCard();

        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findById(creditCard.getId());

        creditCardService.deleteById(creditCard.getId());

        verify(creditCardRepository).findById(creditCard.getId());
        verify(creditCardRepository).deleteById(creditCard.getId());
    }

    @Test
    void deleteNotExistingCreditCard() {
        doReturn(Optional.empty())
                .when(creditCardRepository)
                .findById(DEFAULT_CREDIT_CARD_ID);

        assertThrows(NotFoundException.class, () -> creditCardService.deleteById(DEFAULT_CREDIT_CARD_ID));
    }

    @Test
    void getCreditCardByDriverId() {
        CreditCard creditCard = getCreditCard();
        CreditCardResponse expected = getCreditCardResponse();

        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findByUserIdAndUserType(DEFAULT_DRIVER_ID, DRIVER);
        doReturn(expected)
                .when(creditCardMapper)
                .mapToDto(creditCard);

        CreditCardResponse actual = creditCardService.getDriverCard(DEFAULT_DRIVER_ID);

        assertEquals(expected, actual);
        verify(driverService, times(1)).getDriver(any());
    }

    @Test
    void getCreditCardByDriverIdWhenCardNotExist() {
        doReturn(Optional.empty())
                .when(creditCardRepository)
                .findByUserIdAndUserType(DEFAULT_DRIVER_ID, DRIVER);

        assertThrows(NotFoundException.class, () -> creditCardService.getDriverCard(DEFAULT_DRIVER_ID));
    }

    @Test
    void getCreditCardByPassengerId() {
        CreditCard creditCard = getCreditCard();
        creditCard.setUserType(PASSENGER);
        CreditCardResponse expected = getCreditCardResponse();
        expected.setUserType(PASSENGER.name());

        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findByUserIdAndUserType(DEFAULT_ID, PASSENGER);
        doReturn(expected)
                .when(creditCardMapper)
                .mapToDto(creditCard);

        CreditCardResponse actual = creditCardService.getPassengerCard(DEFAULT_ID);

        assertEquals(expected, actual);
        verify(passengerService, times(1)).getPassenger(any());
    }

    @Test
    void getCreditCardByPassengerIdWhenCardNotExist() {
        doReturn(Optional.empty())
                .when(creditCardRepository)
                .findByUserIdAndUserType(DEFAULT_ID, PASSENGER);

        assertThrows(NotFoundException.class, () -> creditCardService.getPassengerCard(DEFAULT_ID));
    }

    @Test
    void makePaymentSuccessfully() {
        PaymentRequest paymentRequest = getPaymentRequest();
        CreditCard creditCard = getCreditCard();
        creditCard.setUserType(PASSENGER);
        CreditCard creditCardAfterPayment = getCreditCardAfterPayment();
        CreditCardResponse expected = getCreditCardAfterPaymentResponse();
        expected.setUserType(PASSENGER.name());
        Account account = getAccount();
        Account accountAfterPayment = getAccountAfterPayment();
        TransactionStore transaction = getTransaction();

        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findByUserIdAndUserType(paymentRequest.getPassengerId(), PASSENGER);
        doReturn(Optional.of(account))
                .when(accountRepository)
                .findByDriverId(paymentRequest.getDriverId());
        doReturn(creditCardAfterPayment)
                .when(creditCardRepository)
                .save(creditCardAfterPayment);
        doReturn(accountAfterPayment)
                .when(accountRepository)
                .save(accountAfterPayment);
        doReturn(transaction)
                .when(transactionStoreRepository)
                .save(transaction);
        doReturn(expected)
                .when(creditCardMapper)
                .mapToDto(creditCardAfterPayment);

        CreditCardResponse actual = creditCardService.makePayment(paymentRequest);
        assertEquals(expected, actual);
        verify(creditCardRepository, times(1)).save(creditCardAfterPayment);
        verify(accountRepository, times(1)).save(accountAfterPayment);
        verify(transactionStoreRepository, times(1)).save(transaction);
        verify(passengerService, times(1)).getPassenger(any());
    }

    @Test
    void makePaymentWithSumMoreThanCreditCardBalance() {
        PaymentRequest paymentRequest = getPaymentRequest();
        CreditCard creditCard = getCreditCard();
        creditCard.setUserType(PASSENGER);
        creditCard.setBalance(ZERO_BALANCE);
        Account account = getAccount();

        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findByUserIdAndUserType(paymentRequest.getPassengerId(), PASSENGER);
        doReturn(Optional.of(account))
                .when(accountRepository)
                .findByDriverId(paymentRequest.getDriverId());

        assertThrows(BadRequestException.class, () -> creditCardService.makePayment(paymentRequest));

        verify(creditCardRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
        verify(transactionStoreRepository, never()).save(any());
        verify(passengerService, times(1)).getPassenger(any());
    }
}
