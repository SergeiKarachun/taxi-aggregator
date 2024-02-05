package by.sergo.paymentservice.mapper;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.domain.entity.CreditCard;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreditCardMapper {
    private final ModelMapper modelMapper;

    public CreditCardResponse mapToDto(CreditCard creditCard) {
        return modelMapper.map(creditCard, CreditCardResponse.class);
    }

    public CreditCard mapToEntity(CreditCardCreateUpdate dto) {
        return modelMapper.map(dto, CreditCard.class);
    }
}
