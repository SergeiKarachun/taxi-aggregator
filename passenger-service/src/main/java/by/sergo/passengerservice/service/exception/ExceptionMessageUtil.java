package by.sergo.passengerservice.service.exception;

import lombok.experimental.UtilityClass;

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
}
