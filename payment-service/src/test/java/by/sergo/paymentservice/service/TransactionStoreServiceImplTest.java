package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.response.ListTransactionStoreResponse;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.mapper.TransactionStoreMapper;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.service.exception.NotFoundException;
import by.sergo.paymentservice.service.impl.TransactionStoreServiceImpl;
import by.sergo.paymentservice.util.TransactionStoreTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Optional;

import static by.sergo.paymentservice.domain.enums.UserType.*;
import static by.sergo.paymentservice.util.TransactionStoreTestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionStoreServiceImplTest {

    @Mock
    private TransactionStoreMapper transactionStoreMapper;
    @Mock
    private TransactionStoreRepository transactionStoreRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CreditCardRepository creditCardRepository;

    @InjectMocks
    private TransactionStoreServiceImpl transactionStoreService;

    @Test
    void getDriverTransactionByDriverId() {
        Account account = getDriverAccount();
        Page<TransactionStore> transactionStorePage = new PageImpl<>(Arrays.asList(
                getDefaultWithdrawalTransaction()
        ));

        doReturn(Optional.of(account))
                .when(accountRepository)
                .findByDriverId(account.getDriverId());
        when(transactionStoreRepository.findAllByAccountNumber(account.getAccountNumber(), PageRequest.of(VALID_PAGE - 1, VALID_SIZE).withSort(Sort.by(Sort.Order.asc(SORT_BY)))))
                .thenReturn(transactionStorePage);
        doReturn(getDefaultWithdrawalTransactionResponse()).when(transactionStoreMapper).mapToDto(any(TransactionStore.class));

        ListTransactionStoreResponse response = transactionStoreService.getDriverTransactionByDriverId(account.getDriverId(), VALID_PAGE, VALID_SIZE);

        assertNotNull(response);
        assertEquals(1, response.getTransactions().size());
        assertEquals(DEFAULT_ID, response.getTransactions().get(0).getId());
        verify(transactionStoreRepository).findAllByAccountNumber(account.getAccountNumber(), PageRequest.of(VALID_PAGE - 1, VALID_SIZE).withSort(Sort.by(Sort.Order.asc(SORT_BY))));
        verify(transactionStoreMapper, times(1)).mapToDto(any(TransactionStore.class));
    }

    @Test
    void getDriverTransactionByDriverWhenParamIsInvalid() {
        Account account = getDriverAccount();

        doReturn(Optional.of(account))
                .when(accountRepository)
                .findByDriverId(account.getDriverId());

        assertThrows(BadRequestException.class, () -> transactionStoreService.getDriverTransactionByDriverId(DEFAULT_DRIVER_ID, INVALID_PAGE, INVALID_SIZE));
    }

    @Test
    void getDriverTransactionByDriverWhenDriverNotFound() {
        assertThrows(NotFoundException.class, () -> transactionStoreService.getDriverTransactionByDriverId(DEFAULT_DRIVER_ID, INVALID_PAGE, INVALID_SIZE));
    }

    @Test
    void getPassengerTransactionByPassengerId() {
        Page<TransactionStore> transactionStorePage = new PageImpl<>(Arrays.asList(
                getDefaultPaymentTransaction()
        ));
        CreditCard creditCard = TransactionStoreTestUtil.getPassengerCreditCard();

        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findByUserIdAndUserType(DEFAULT_PASSENGER_ID, PASSENGER);
        when(transactionStoreRepository.findAllByCreditCardNumber(creditCard.getCreditCardNumber(), PageRequest.of(VALID_PAGE - 1, VALID_SIZE).withSort(Sort.by(Sort.Order.asc(SORT_BY)))))
                .thenReturn(transactionStorePage);
        doReturn(getDefaultPaymentTransactionResponse()).when(transactionStoreMapper).mapToDto(any(TransactionStore.class));

        ListTransactionStoreResponse response = transactionStoreService.getPassengerTransactionByPassengerId(DEFAULT_PASSENGER_ID, VALID_PAGE, VALID_SIZE);

        assertNotNull(response);
        assertEquals(1, response.getTransactions().size());
        assertEquals(DEFAULT_ID, response.getTransactions().get(0).getId());
        verify(transactionStoreRepository).findAllByCreditCardNumber(DEFAULT_CREDIT_CARD_NUMBER, PageRequest.of(VALID_PAGE - 1, VALID_SIZE).withSort(Sort.by(Sort.Order.asc(SORT_BY))));
        verify(transactionStoreMapper, times(1)).mapToDto(any(TransactionStore.class));
    }

    @Test
    void getPassengerTransactionByPassengerIdWhenParamIsInvalid() {
        CreditCard creditCard = TransactionStoreTestUtil.getPassengerCreditCard();

        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findByUserIdAndUserType(DEFAULT_PASSENGER_ID, PASSENGER);

        assertThrows(BadRequestException.class, () -> transactionStoreService.getPassengerTransactionByPassengerId(DEFAULT_PASSENGER_ID, INVALID_PAGE, INVALID_SIZE));
    }

    @Test
    void getPassengerTransactionByPassengerIdWhenPassengerNotFound() {
        assertThrows(NotFoundException.class, () -> transactionStoreService.getPassengerTransactionByPassengerId(DEFAULT_PASSENGER_ID, INVALID_PAGE, INVALID_SIZE));
    }
}
