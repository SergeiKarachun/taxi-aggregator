package by.sergo.paymentservice.mapper;

import by.sergo.paymentservice.domain.dto.response.TransactionStoreResponse;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionStoreMapper {
    private final ModelMapper modelMapper;

    public TransactionStoreResponse mapToDto(TransactionStore transactionStore) {
        return modelMapper.map(transactionStore, TransactionStoreResponse.class);
    }
}
