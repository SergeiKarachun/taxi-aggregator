package by.sergo.paymentservice.service.exception;

import lombok.experimental.UtilityClass;

import java.util.HashMap;

@UtilityClass
public class ExceptionMessageUtil {

    public static String getAlreadyExistMessage(String className, String variableName, String variableValue) {
        return className + " with this " + variableName + " " + variableValue + " already exists.";
    }

    public static String getWithdrawalExceptionMessage(String className, String variableName, String variableValue, String balance) {
        return className + " with " + variableName + " " + variableValue + " has insufficient funds for withdrawal. Balance is " + balance;
    }

    public static String getExpirationCardExceptionMessage(String className, String variableName, String variableValue) {
        return className + " with " + variableName + " " + variableValue + " not valid yet";
    }
    public static String getNotFoundMessage(String className, String variableName, Long id) {
        return className + " with " + variableName + " " + id + " doesn't exist.";
    }

    public static String getNotFoundMessage(String className, String variableName, String variableValue) {
        return className + " with " + variableName + " " + variableValue + " doesn't exist.";
    }

    public static String getInvalidRequestMessage(Integer page, Integer size) {
        return "Request parameters must be greater than 0, your parameters page=" + page + " size=" + size + ".";
    }

    public static String getInvalidSortingParamRequestMessage(String field) {
        return "Invalid sorting parameter, can't sort by " + field + " parameter.";
    }

    public static String getAlreadyExistMapMessage(HashMap<String, String> errors) {
        StringBuilder message = new StringBuilder();
        errors.values().forEach(str -> message.append(str));
        return message.toString();
    }
}
