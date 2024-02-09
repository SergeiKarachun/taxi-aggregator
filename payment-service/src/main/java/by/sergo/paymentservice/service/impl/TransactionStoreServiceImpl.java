package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.domain.dto.response.ListTransactionStoreResponse;
import by.sergo.paymentservice.mapper.TransactionStoreMapper;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import by.sergo.paymentservice.service.TransactionStoreService;
import by.sergo.paymentservice.service.exception.BadRequestException;
import by.sergo.paymentservice.util.ExceptionMessageUtil;
import by.sergo.paymentservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static by.sergo.paymentservice.domain.enums.UserType.PASSENGER;

@Service
@RequiredArgsConstructor
public class TransactionStoreServiceImpl implements TransactionStoreService {
    private final TransactionStoreMapper transactionStoreMapper;
    private final TransactionStoreRepository transactionStoreRepository;
    private final AccountRepository accountRepository;
    private final CreditCardRepository creditCardRepository;

    @Override
    public ListTransactionStoreResponse getDriverTransactionByDriverId(Long driverId, Integer page, Integer size) {
        var accountByDriverId = accountRepository.findByDriverId(driverId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Account", "driverId", driverId)));
        var pageRequest = getPageRequest(page, size);
        var responsePage = transactionStoreRepository.findAllByAccountNumber(accountByDriverId.getAccountNumber(), pageRequest)
                .map(transactionStoreMapper::mapToDto);

        return ListTransactionStoreResponse.builder()
                .transactions(responsePage.getContent())
                //.page(responsePage.getPageable().getPageNumber() + 1)
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField("operationDate")
                .build();
    }

    @Override
    public ListTransactionStoreResponse getPassengerTransactionByPassengerId(Long passengerId, Integer page, Integer size) {
        var creditCard = creditCardRepository.findByUserIdAndUserType(passengerId, PASSENGER)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Credit card", "passengerId", passengerId)));
        var pageRequest = getPageRequest(page, size);
        var responsePage = transactionStoreRepository.findAllByCreditCardNumber(creditCard.getCreditCardNumber(), pageRequest)
                .map(transactionStoreMapper::mapToDto);

        return ListTransactionStoreResponse.builder()
                .transactions(responsePage.getContent())
                .totalPages(responsePage.getTotalPages())
                .size(responsePage.getContent().size())
                .total((int) responsePage.getTotalElements())
                .sortedByField("operationDate")
                .build();
    }

    private PageRequest getPageRequest(Integer page, Integer size) {
        if (page < 1 || size < 1) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidRequestMessage(page, size));
        } else {
            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc("operationDate")));
        }
    }
}