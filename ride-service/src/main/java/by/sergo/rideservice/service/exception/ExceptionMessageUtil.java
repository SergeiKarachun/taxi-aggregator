package by.sergo.rideservice.service.exception;

import lombok.experimental.UtilityClass;

import java.util.HashMap;

@UtilityClass
public class ExceptionMessageUtil {

    public static String getAlreadyExistMessage(String className, String variableName, String variableValue) {
        return className + " with this " + variableName + " " + variableValue + " already exists.";
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
        errors.values().forEach(str -> message.append(str).append(0xA));
        return message.toString();
    }
}
