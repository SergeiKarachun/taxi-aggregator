package by.sergo.passengerservice.service.exception;

import lombok.experimental.UtilityClass;

import java.util.HashMap;

@UtilityClass
public class ExceptionMessageUtil {

    public static String getAlreadyExistMessage(String className, String variableName, String variableValue) {
        return "%s with this %s %s already exists.".formatted(className, variableName, variableValue);
    }

    public static String getNotFoundMessage(String className, String variableName, Long id) {
        return "%s with %s %d doesn't exist.".formatted(className, variableName, id);
    }

    public static String getNotFoundMessage(String className, String variableName, String variableValue) {
        return "%s with %s %s doesn't exist.".formatted(className, variableName, variableValue);
    }

    public static String getInvalidRequestMessage(Integer page, Integer size) {
        return "Request parameters must be greater than 0, your parameters page=%d size=%d.".formatted(page, size);
    }

    public static String getInvalidSortingParamRequestMessage(String field) {
        return "Invalid sorting parameter, can't sort by %s parameter.".formatted(field);
    }

    public static String getAlreadyExistMapMessage(HashMap<String, String> errors) {
        StringBuilder message = new StringBuilder();
        errors.values().forEach(str -> message.append(str).append("."));
        return message.toString();
    }
}
