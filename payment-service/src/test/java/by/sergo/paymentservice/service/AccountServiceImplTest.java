package by.sergo.paymentservice.service;

import by.sergo.paymentservice.client.DriverFeignClient;
import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequest;
import by.sergo.paymentservice.domain.dto.response.AccountResponse;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.mapper.AccountMapper;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.service.exception.NotFoundException;
import by.sergo.paymentservice.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static by.sergo.paymentservice.util.AccountTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private TransactionStoreRepository transactionStoreRepository;
    @Mock
    private DriverFeignClient driverFeignClient;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccountSuccessfully() {
        AccountCreateUpdateRequest request = getAccountCreateRequest();
        Account account = getAccount();
        AccountResponse response = getAccountResponse();

        doReturn(false)
                .when(accountRepository)
                .existsByDriverId(request.getDriverId());
        doReturn(account)
                .when(accountMapper)
                .mapToEntity(request);
        doReturn(account)
                .when(accountRepository)
                .save(account);
        doReturn(response)
                .when(accountMapper)
                .mapToDto(account);

        AccountResponse actual = accountService.createAccount(request);

        assertEquals(response, actual);
        verify(driverFeignClient, times(1)).getDriverById(request.getDriverId());
    }

    @Test
    void createAccountWhenDriverAccountAlreadyExists() {
        AccountCreateUpdateRequest request = getAccountCreateRequest();
        doReturn(true)
                .when(accountRepository)
                .existsByDriverId(request.getDriverId());

        assertThrows(BadRequestException.class, () -> accountService.createAccount(request));

        verify(accountRepository, times(1)).existsByDriverId(request.getDriverId());
    }

    @Test
    void deleteAccountWhenAccountExists() {
        Account account = getAccount();

        doReturn(Optional.of(account))
                .when(accountRepository)
                .findById(account.getId());

        accountService.deleteById(account.getId());

        verify(accountRepository, times(1)).findById(account.getId());
        verify(accountRepository, times(1)).deleteById(account.getId());
    }

    @Test
    void deleteAccountWhenAccountNotFound() {
        doReturn(Optional.empty())
                .when(accountRepository)
                .findById(DEFAULT_ID);

        assertThrows(NotFoundException.class, () -> accountService.deleteById(DEFAULT_ID));
    }

    @Test
    void withdrawalBalanceFromAccountWhenBalanceMoreThanSumToWithdrawal() {
        Account account = getAccount();
        CreditCard creditCard = getCreditCard();
        Account accountAfterWithdrawal = getAccountAfterWithdrawal();
        AccountResponse expected = getAccountResponseAfterWithdrawal();
        CreditCard creditCardAfterWithdrawal = getCreditCardAfterWithdrawal();
        TransactionStore transaction = getTransaction();

        doReturn(Optional.of(account))
                .when(accountRepository)
                .findByDriverId(account.getDriverId());
        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findByUserIdAndUserType(creditCard.getUserId(), creditCard.getUserType());
        doReturn(accountAfterWithdrawal)
                .when(accountRepository)
                .save(accountAfterWithdrawal);
        doReturn(creditCardAfterWithdrawal)
                .when(creditCardRepository)
                .save(creditCardAfterWithdrawal);
        doReturn(transaction)
                .when(transactionStoreRepository)
                .save(transaction);
        doReturn(expected)
                .when(accountMapper)
                .mapToDto(accountAfterWithdrawal);

        AccountResponse actual = accountService.withdrawalBalance(DEFAULT_DRIVER_ID, DEFAULT_VALUE);

        assertEquals(expected, actual);
        verify(accountRepository, times(1)).save(accountAfterWithdrawal);
        verify(creditCardRepository, times(1)).save(creditCardAfterWithdrawal);
        verify(transactionStoreRepository, times(1)).save(transaction);
        verify(driverFeignClient, times(1)).getDriverById(DEFAULT_ID);
    }

    @Test
    void withdrawalBalanceFromAccountWhenBalanceLessThanSumToWithdrawal() {
        Account account = getAccountAfterWithdrawal();
        CreditCard creditCard = getCreditCard();

        doReturn(Optional.of(account))
                .when(accountRepository)
                .findByDriverId(account.getDriverId());
        doReturn(Optional.of(creditCard))
                .when(creditCardRepository)
                .findByUserIdAndUserType(creditCard.getUserId(), creditCard.getUserType());

        assertThrows(BadRequestException.class, () -> accountService.withdrawalBalance(DEFAULT_DRIVER_ID, DEFAULT_VALUE));

        verify(driverFeignClient, times(1)).getDriverById(DEFAULT_ID);
        verify(creditCardRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
        verify(transactionStoreRepository, never()).save(any());
    }

    @Test
    void withdrawalBalanceFromAccountWhenAccountNotFound() {
        doReturn(Optional.empty())
                .when(accountRepository)
                .findByDriverId(DEFAULT_DRIVER_ID);

        assertThrows(NotFoundException.class, () -> accountService.withdrawalBalance(DEFAULT_DRIVER_ID, DEFAULT_VALUE));
    }

    @Test
    void getAccountByDriverId() {
        Account account = getAccount();
        AccountResponse expected = getAccountResponse();

        doReturn(Optional.of(account))
                .when(accountRepository)
                .findByDriverId(account.getDriverId());
        doReturn(expected)
                .when(accountMapper)
                .mapToDto(account);

        AccountResponse actual = accountService.getByDriverId(account.getDriverId());

        assertEquals(expected, actual);
        verify(accountRepository, times(1)).findByDriverId(DEFAULT_DRIVER_ID);
        verify(accountMapper, times(1)).mapToDto(account);
    }

    @Test
    void getAccountByDriverIdWhenDriverNotFound() {
        doReturn(Optional.empty())
                .when(accountRepository)
                .findByDriverId(DEFAULT_DRIVER_ID);

        assertThrows(NotFoundException.class, () -> accountService.getByDriverId(DEFAULT_DRIVER_ID));
    }

    @Test
    void getAccountById() {
        Account account = getAccount();
        AccountResponse expected = getAccountResponse();

        doReturn(Optional.of(account))
                .when(accountRepository)
                .findById(DEFAULT_ID);
        doReturn(expected)
                .when(accountMapper)
                .mapToDto(account);

        AccountResponse actual = accountService.getById(DEFAULT_ID);

        assertEquals(expected, actual);
        verify(accountRepository, times(1)).findById(DEFAULT_ID);
        verify(accountMapper, times(1)).mapToDto(account);
    }

    @Test
    void getAccountByIdWhenAccountNotFound() {
        doReturn(Optional.empty())
                .when(accountRepository)
                .findByDriverId(DEFAULT_ID);

        assertThrows(NotFoundException.class, () -> accountService.getByDriverId(DEFAULT_ID));
    }
}
