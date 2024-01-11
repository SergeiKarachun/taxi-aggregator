package by.sergo.rideservice.service.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessageUtil {

    public static String getNotFoundMessage(String className, String variableName, String variableValue) {
        return className + " with " + variableName + " " + variableValue + " doesn't exist.";
    }

    public static String getInvalidRequestMessage(Integer page, Integer size) {
        return "Request parameters must be greater than 0, your parameters page=" + page + " size=" + size + ".";
    }

    public static String getInvalidSortingParamRequestMessage(String field) {
        return "Invalid sorting parameter, can't sort by " + field + " parameter.";
    }

    public static String getInvalidStatusMessage(String status) {
        return "Invalid status parameter = " + status + ". Status can be CREATED|ACCEPTED|TRANSPORT|REJECTED|FINISHED.";
    }
}
