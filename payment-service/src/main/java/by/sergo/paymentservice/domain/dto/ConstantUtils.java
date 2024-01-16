package by.sergo.paymentservice.domain.dto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtils {
    public static final String CREDIT_CARD_NUMBER_PATTERN = "^\\d{16}$";
    public static final String CVV_PATTERN = "^\\d{3}$";
    public static final String USER_TYPE_PATTERN = "^DRIVER|PASSENGER$";
}
