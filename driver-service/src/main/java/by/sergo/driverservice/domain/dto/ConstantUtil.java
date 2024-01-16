package by.sergo.driverservice.domain.dto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtil {
    public static final String NUMBER_PATTERN = "^\\d{4} [A-Z]{2}-\\d{1}$";
    public static final String COLOR_PATTERN = "^(RED|BLACK|WHITE|BLUE|SILVER|YELLOW|GREEN)$";
    public static final String EMAIL_EXAMPLE = "example@gmail.com";
    public static final String PHONE_PATTERN = "^\\+375(29|33|44|25)(\\d{7})$";
}
