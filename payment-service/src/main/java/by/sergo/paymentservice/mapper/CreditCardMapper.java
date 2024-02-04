package by.sergo.paymentservice.mapper;

import by.sergo.paymentservice.client.DriverFeignClient;
import by.sergo.paymentservice.client.PassengerFeignClient;
import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import by.sergo.paymentservice.domain.entity.CreditCard;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import static by.sergo.paymentservice.domain.enums.UserType.PASSENGER;

@Component
@RequiredArgsConstructor
public class CreditCardMapper {
    private final ModelMapper modelMapper;
    private final PassengerFeignClient passengerFeignClient;
    private final DriverFeignClient driverFeignClient;

    public CreditCardResponse mapToDto(CreditCard creditCard) {


        CreditCardResponse response = modelMapper.map(creditCard, CreditCardResponse.class);
        if (creditCard.getUserType().equals(PASSENGER)) {
            response.setUser(getPassengerById(creditCard.getUserId()));
        } else {
            response.setUser(getDriverById(creditCard.getUserId()));

        }
        return response;
    }

    public CreditCard mapToEntity(CreditCardCreateUpdate dto) {
        return modelMapper.map(dto, CreditCard.class);
    }

    private UserResponse getPassengerById(Long id) {
        return passengerFeignClient.getPassengerById(id);
    }

    private UserResponse getDriverById(Long id) {
        return driverFeignClient.getDriverById(id);
    }

}
