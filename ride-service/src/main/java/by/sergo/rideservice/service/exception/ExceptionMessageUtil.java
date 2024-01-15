package by.sergo.rideservice.service.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessageUtil {

    public static String getNotFoundMessage(String className, String variableName, String variableValue) {
        return "%s with %s %s doesn't exist.".formatted(className, variableName, variableValue);
    }

    public static String getInvalidRequestMessage(Integer page, Integer size) {
        return "Request parameters must be greater than 0, your parameters page=%d size=%d.".formatted(page, size);
    }

    public static String getInvalidSortingParamRequestMessage(String field) {
        return "Invalid sorting parameter, can't sort by %s parameter.".formatted(field);
    }

    public static String getInvalidStatusMessage(String status) {
        return "Invalid status parameter = %s. Status can be CREATED|ACCEPTED|TRANSPORT|REJECTED|FINISHED.".formatted(status);
    }

    public static String canNotChangeStatusMessage(String status, String odlStatus, String shouldBeStatus) {
        return "Can't change status to %s. Status is %s, ride status for change should be: %s".formatted(status, odlStatus, shouldBeStatus);
    }

    public static String alreadyHasDriver(String rideId) {
        return "Ride with rideId %s already has driver.".formatted(rideId);
    }
}
