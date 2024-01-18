package by.sergo.paymentservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtils {
    public static final String CREDIT_CARD_NUMBER_PATTERN = "^\\d{16}$";
    public static final String CVV_PATTERN = "^\\d{3}$";
    public static final String USER_TYPE_PATTERN = "^DRIVER|PASSENGER$";
    public static final String MIN_BALANCE = "0.0";
    public static final String MIN_PRICE = "2.70";
}
