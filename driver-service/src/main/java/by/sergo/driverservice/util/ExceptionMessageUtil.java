package by.sergo.driverservice.util;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class ExceptionMessageUtil {

    public static final String CAN_NOT_CREATE_CAR_MESSAGE = "Can't create new car, please check input parameters.";
    public static final String CAN_NOT_UPDATE_CAR_MESSAGE = "Can't update new car, please check input parameters.";
    public static final String YEAR_SHOULD_BE_MESSAGE = "The year of manufacture should be no more than now.";
    public final static String CAN_NOT_CREATE_DRIVER_MESSAGE = "Can't create new driver, please check input parameters";

    public static String getAlreadyExistMessage(String className, String variableName, String variableValue) {
        return "%s with this %s %s already exists.".formatted(className, variableName, variableValue);
    }

    public static String getNotFoundMessage(String className, String variableName, Long variableValue) {
        return "%s with %s=%d doesn't exist.".formatted(className, variableName, variableValue);
    }


    public static String getInvalidRequestMessage(Integer page, Integer size) {
        return "Request parameters must be greater than 0, your parameters page=%d size=%d.".formatted(page, size);

    }

    public static String getInvalidSortingParamRequestMessage(String orderBy) {
        return "Invalid sorting parameter, can't sort by %s parameter.".formatted(orderBy);
    }

    public static String getAlreadyExistMapMessage(Map<String, String> errors) {
        StringBuilder message = new StringBuilder();
        errors.values().forEach(message::append);
        return message.toString();
    }
}
