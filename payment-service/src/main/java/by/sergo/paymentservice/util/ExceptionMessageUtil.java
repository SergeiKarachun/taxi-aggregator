package by.sergo.paymentservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessageUtil {

    public static String getAlreadyExistMessage(String className, String variableName, String variableValue) {
        return "%s with this %s %s already exists.".formatted(className, variableName, variableValue);
    }

    public static String getAlreadyExistMessage(String className, String variableName, Long variableValue, String variableNameSecond, String variableValueSecond) {
        return "%s with %s %d and %s %s already exists.".formatted(className, variableName, variableValue, variableNameSecond, variableValueSecond);
    }

    public static String getWithdrawalExceptionMessage(String className, String variableName, String variableValue, String balance) {
        return "%s with %s %s has insufficient funds for withdrawal. Balance is %s".formatted(className, variableName, variableValue, balance);
    }

    public static String getExpirationCardExceptionMessage(String className, String variableName, String variableValue) {
        return "%s with %s %s not valid yet".formatted(className, variableName, variableValue);
    }
    public static String getNotFoundMessage(String className, String variableName, Long id) {
        return "%s with %s %d doesn't exist.".formatted(className, variableName, id);
    }

    public static String getInvalidRequestMessage(Integer page, Integer size) {
        return "Request parameters must be greater than 0, your parameters page=%d size=%d.".formatted(page, size);
    }
}
