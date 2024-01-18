package by.sergo.paymentservice.mapper;

import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequest;
import by.sergo.paymentservice.domain.dto.response.AccountResponse;
import by.sergo.paymentservice.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final ModelMapper modelMapper;

    public Account mapToEntity(AccountCreateUpdateRequest dto) {
        return modelMapper.map(dto, Account.class);
    }

    public AccountResponse mapToDto(Account account) {
        return modelMapper.map(account, AccountResponse.class);
    }
}
