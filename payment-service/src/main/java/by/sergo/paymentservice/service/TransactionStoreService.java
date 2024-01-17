package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.response.ListTransactionStoreResponse;

public interface TransactionStoreService {
    ListTransactionStoreResponse getDriverTransactionByDriverId(Long driverId, Integer page, Integer size);

    ListTransactionStoreResponse getPassengerTransactionByPassengerId(Long passengerId, Integer page, Integer size);
}
