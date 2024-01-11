package by.sergo.paymentservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ListTransactionStoreResponseDto {
    private Integer page;
    private Integer totalPages;
    private Integer size;
    private Integer total;
    private String sortedByField;
    private List<TransactionStoreResponseDto> transactions;
}
