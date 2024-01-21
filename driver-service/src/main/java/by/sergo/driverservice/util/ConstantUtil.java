package by.sergo.driverservice.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class ConstantUtil {
    public static final String NUMBER_PATTERN = "^\\d{4} [A-Z]{2}-\\d{1}$";
    public static final String COLOR_PATTERN = "^(RED|BLACK|WHITE|BLUE|SILVER|YELLOW|GREEN)$";
    public static final String EMAIL_EXAMPLE = "example@gmail.com";
    public static final String PHONE_PATTERN = "^\\+375(29|33|44|25)(\\d{7})$";
    public static final Double DEFAULT_RATING = 5.0;
    public static final BigDecimal DEFAULT_BALANCE = BigDecimal.ZERO;
}
