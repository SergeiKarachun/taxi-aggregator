package by.sergo.rideservice.util;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.DriverForRideResponse;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.CreditCardResponse;
import by.sergo.rideservice.domain.dto.response.DriverResponse;
import by.sergo.rideservice.domain.dto.response.PassengerResponse;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import by.sergo.rideservice.domain.enums.Status;
import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static by.sergo.rideservice.domain.enums.PaymentMethod.*;
import static by.sergo.rideservice.domain.enums.Status.CREATED;
import static by.sergo.rideservice.domain.enums.Status.FINISHED;

@UtilityClass
public class RideTestUtil {

    public final String DEFAULT_RIDE_ID = "65afd8b6759a765221df8051";
    public final ObjectId RIDE_ID = new ObjectId(DEFAULT_RIDE_ID);
    public final Long DEFAULT_ID = 1L;
    public final Long DEFAULT_PASSENGER_ID = 1L;
    public final String DEFAULT_NAME = "Petr";
    public final String DEFAULT_SURNAME = "Petrov";
    public final String DEFAULT_EMAIL = "petr@gmail.com";
    public final String DEFAULT_PHONE = "+375331234567";
    public final Double DEFAULT_RATING = 5.0;
    public final String PICK_UP_ADDRESS = "home";
    public final String DESTINATION_ADDRESS = "work";
    public final Double PRICE = 5.50;
    public final Status DEFAULT_STATUS = CREATED;
    public final Long DEFAULT_CREDIT_CARD_ID = 1L;
    public final String DEFAULT_CREDIT_CARD_NUMBER = "2844575209195922";
    public final String CVV = "111";
    public final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(50);
    public final String DEFAULT_USER_TYPE = "PASSENGER";
    public final int INVALID_PAGE = -1;
    public final int VALID_PAGE = 1;
    public final int INVALID_SIZE = -1;
    public final int VALID_SIZE = 10;
    public final String INVALID_ORDER_BY = "priiice";
    public final String VALID_ORDER_BY = "price";


    public RideCreateUpdateRequest getRideCreateRequest() {
        return RideCreateUpdateRequest.builder()
                .passengerId(DEFAULT_PASSENGER_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .paymentMethod(CARD.name())
                .build();
    }

    public RideCreateUpdateRequest getRideUpdateRequest() {
        return RideCreateUpdateRequest.builder()
                .passengerId(DEFAULT_PASSENGER_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .paymentMethod(CASH.name())
                .build();
    }

    public Ride getDefaultRideToSave() {
        return Ride.builder()
                .id(RIDE_ID)
                .pickUpAddress(PICK_UP_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .passengerId(DEFAULT_PASSENGER_ID)
                .paymentMethod(CASH)
                .status(CREATED)
                .build();
    }

    public RideResponse getDefaultRideResponse() {
        return RideResponse.builder()
                .id(DEFAULT_RIDE_ID)
                .status(DEFAULT_STATUS)
                .price(PRICE)
                .destinationAddress(DESTINATION_ADDRESS)
                .pickUpAddress(PICK_UP_ADDRESS)
                .paymentMethod(CASH.name())
                .passenger(getDefaultPassengerResponse())
                .build();
    }

    public Ride getDefaultRide() {
        return Ride.builder()
                .id(RIDE_ID)
                .status(DEFAULT_STATUS)
                .price(PRICE)
                .destinationAddress(DESTINATION_ADDRESS)
                .pickUpAddress(PICK_UP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .paymentMethod(CARD)
                .creatingTime(LocalDateTime.now())
                .build();
    }

    public Ride getSecondRide() {
        return Ride.builder()
                .id(RIDE_ID)
                .status(DEFAULT_STATUS)
                .price(PRICE)
                .destinationAddress(DESTINATION_ADDRESS)
                .pickUpAddress(PICK_UP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .driverId(DEFAULT_ID)
                .paymentMethod(CARD)
                .creatingTime(LocalDateTime.now())
                .build();
    }

    public DriverResponse getDefaultDriverResponse() {
        return DriverResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public PassengerResponse getDefaultPassengerResponse() {
        return PassengerResponse.builder()
                .id(DEFAULT_PASSENGER_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public CreditCardResponse getCreditCardResponse() {
        return CreditCardResponse.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .userId(DEFAULT_PASSENGER_ID)
                .userType(DEFAULT_USER_TYPE)
                .build();
    }

    public DriverForRideResponse getDriverForRideResponse() {
        return DriverForRideResponse.builder()
                .rideId(DEFAULT_RIDE_ID)
                .driverId(DEFAULT_ID)
                .build();
    }

    public Ride getFinishedRide() {
        return Ride.builder()
                .id(RIDE_ID)
                .status(FINISHED)
                .price(PRICE)
                .destinationAddress(DESTINATION_ADDRESS)
                .pickUpAddress(PICK_UP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .driverId(DEFAULT_ID)
                .paymentMethod(CARD)
                .creatingTime(LocalDateTime.now())
                .build();
    }
}
