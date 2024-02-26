package by.sergo.rideservice.component;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.RideListResponse;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import by.sergo.rideservice.domain.enums.PaymentMethod;
import by.sergo.rideservice.domain.enums.Status;
import by.sergo.rideservice.kafka.RideProducer;
import by.sergo.rideservice.kafka.StatusProducer;
import by.sergo.rideservice.mapper.RideMapper;
import by.sergo.rideservice.repository.RideRepository;
import by.sergo.rideservice.service.DriverService;
import by.sergo.rideservice.service.PassengerService;
import by.sergo.rideservice.service.PaymentService;
import by.sergo.rideservice.service.exception.BadRequestException;
import by.sergo.rideservice.service.exception.NotFoundException;
import by.sergo.rideservice.service.impl.RideServiceImpl;
import by.sergo.rideservice.util.ExceptionMessageUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static by.sergo.rideservice.domain.enums.Status.*;
import static by.sergo.rideservice.util.RideTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@CucumberContextConfiguration
public class RideComponentTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private RideMapper rideMapper;
    @Mock
    private StatusProducer statusProducer;
    @Mock
    private RideProducer rideProducer;
    @Mock
    private PassengerService passengerService;
    @Mock
    private DriverService driverService;
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private RideServiceImpl rideService;

    private RideResponse rideResponse;
    private RideListResponse rideListResponse;
    private Exception exception;

    @Given("A ride with id {string} exists")
    public void rideWithIdExists(String id) {
        RideResponse expected = getDefaultRideResponse();
        Ride retrievedRide = getDefaultRide();
        doReturn(Optional.of(retrievedRide))
                .when(rideRepository)
                .findById(id);
        doReturn(true)
                .when(rideRepository)
                .existsById(id);
        doReturn(expected)
                .when(rideMapper)
                .mapToDto(any(Ride.class));

        Optional<Ride> ride = rideRepository.findById(id);
        assertTrue(ride.isPresent());
    }

    @When("The id {string} is passed to the findById method")
    public void theIdIsPassedToTheFindByIdMethod(String id) {
        try {
            rideResponse = rideService.getById(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain ride with id {string}")
    public void theResponseShouldContainRideWithId(String id) {
        Ride driver = rideRepository.findById(id).get();
        RideResponse expected = rideMapper.mapToDto(driver);

        assertEquals(rideResponse, expected);
    }

    @Given("A ride with id {string} doesn't exist")
    public void aRideWithIdDoesnTExist(String id) {
        Optional<Ride> ride = rideRepository.findById(id);
        assertFalse(ride.isPresent());
    }

    @Then("The NotFoundException with id {string} should be thrown")
    public void theNotFoundExceptionWithIdShouldBeThrown(String id) {
        String expected = ExceptionMessageUtil.getNotFoundMessage("Ride", "id", id);
        String actual = exception.getMessage();

        assertEquals(actual, expected);
    }

    @When("The id {string} is passed to the deleteById method")
    public void theIdIsPassedToTheDeleteByIdMethod(String id) {
        try {
            rideResponse = rideService.deleteById(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain response with id {string}")
    public void theResponseShouldContainMessageWithId(String id) {
        RideResponse expected = getDefaultRideResponse();

        assertEquals(expected.getId(), id);
        assertEquals(expected, rideResponse);
    }

    @Given("Create ride with default data")
    public void createRideWithDefaultData() {
        Ride createdRide = getDefaultRideToSave();
        createdRide.setPaymentMethod(PaymentMethod.CASH);
        RideResponse expected = getDefaultRideResponse();
        Ride savedRide = getDefaultRide();
        doReturn(createdRide)
                .when(rideMapper)
                .mapToEntity(any(RideCreateUpdateRequest.class));
        doReturn(savedRide)
                .when(rideRepository)
                .save(createdRide);
        doReturn(expected)
                .when(rideMapper)
                .mapToDto(any(Ride.class));
    }

    @Then("The response should contain created ride")
    public void theResponseShouldContainCreatedRide() {
        RideResponse expected = getDefaultRideResponse();
        assertEquals(rideResponse.getDestinationAddress(), expected.getDestinationAddress());
        assertEquals(rideResponse.getPickUpAddress(), expected.getPickUpAddress());
        assertEquals(rideResponse.getPrice(), expected.getPrice());
        assertEquals(rideResponse.getPaymentMethod(), expected.getPaymentMethod());
    }

    @When("A create request is passed to the add method")
    public void aCreateRequestIsPassedToTheAddMethod() {
        RideCreateUpdateRequest createRequest = getRideCreateRequest();
        createRequest.setPaymentMethod("CASH");
        try {
            rideResponse = rideService.create(createRequest);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @When("An update request for ride with id {string} is passed to the update method")
    public void anUpdateRequestForRideWithIdIsPassedToTheUpdateMethod(String id) {
        var request = getRideUpdateRequest();
        try {
            rideResponse = rideService.update(request, id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Given("A ride with id {string} for update exists")
    public void aRideWithIdForUpdateExists(String id) {
        Ride rideToSave = getDefaultRideToSave();
        Ride savedRide = getDefaultRide();
        RideResponse response = getDefaultRideResponse();
        doReturn(Optional.of(getDefaultRide()))
                .when(rideRepository)
                .findById(id);
        doReturn(rideToSave)
                .when(rideMapper)
                .mapToEntity(any(RideCreateUpdateRequest.class));
        doReturn(savedRide)
                .when(rideRepository)
                .save(rideToSave);
        doReturn(response)
                .when(rideMapper)
                .mapToDto(any(Ride.class));

        Optional<Ride> ride = rideRepository.findById(id);
        assertTrue(ride.isPresent());
    }

    @Then("The response should contain updated ride with id {string}")
    public void theResponseShouldContainUpdatedRideWithId(String id) {
        RideResponse actual = rideMapper.mapToDto(rideRepository.findById(id).get());

        assertEquals(actual, rideResponse);
    }

    @When("Ride id {string} is passed to the rejectRide method")
    public void rideIdIsPassedToTheRejectRideMethod(String id) {
        try {
            rideResponse = rideService.rejectRide(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Given("A ride with id {string} for editing status exists")
    public void aRideWithIdForEditingStatusExists(String id) {
        Ride ride = getDefaultRide();
        RideResponse response = getDefaultRideResponse();
        response.setStatus(Status.REJECTED);
        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(id);
        doReturn(ride)
                .when(rideRepository)
                .save(any(Ride.class));
        doReturn(response)
                .when(rideMapper)
                .mapToDto(any(Ride.class));
    }

    @Given("A ride with id {string} for editing status exists when status created")
    public void aRideWithIdForEditingStatusExistsWhenStatusCreated(String id) {
        Ride createdRide = getDefaultRide();
        doReturn(Optional.of(createdRide))
                .when(rideRepository)
                .findById(id);
    }

    @When("Ride id {string} is passed to the startRide method")
    public void rideIdIsPassedToTheStartRideMethod(String id) {
        try {
            rideResponse = rideService.startRide(id);
        } catch (NotFoundException | BadRequestException e) {
            exception = e;
        }
    }

    @Then("The BadRequestException should be thrown")
    public void theBadRequestExceptionShouldBeThrown() {
        String expected = ExceptionMessageUtil.canNotChangeStatusMessage(TRANSPORT.toString(), CREATED.toString(), ACCEPTED.toString());

        assertEquals(expected, exception.getMessage());

    }

    @Given("History of rides for passenger with id {long}")
    public void historyOfRidesForPassengerWithId(long id) {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(getDefaultRide(), getFinishedRide()));
        doReturn(ridePage)
                .when(rideRepository)
                .findAllByPassengerIdAndStatus(eq(id), any(Status.class), any(PageRequest.class));
        doReturn(getDefaultRideResponse())
                .when(rideMapper)
                .mapToDto(any(Ride.class));
    }

    @When("The getByPassengerId method is called for passenger with id {long}")
    public void theGetByPassengerIdMethodIsCalledForPassengerWithId(long id) {
        try {
            rideListResponse = rideService.getByPassengerId(id, FINISHED.toString(), VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
        } catch (InvalidRequestException e) {
            exception = e;
        }
    }

    @Then("Rides for passenger is returned")
    public void passengerSHistoryIsReturned() {
        assertEquals(rideListResponse.getRides().size(), 2);
    }

    @Given("History of rides for driver with id {long}")
    public void historyOfRidesForDriverWithId(long id) {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(getDefaultRide(), getFinishedRide()));
        doReturn(ridePage)
                .when(rideRepository)
                .findAllByDriverIdAndStatus(eq(id), any(Status.class), any(PageRequest.class));
        doReturn(getDefaultRideResponse())
                .when(rideMapper)
                .mapToDto(any(Ride.class));
    }

    @When("The getByDriverId method is called for driver with id {long}")
    public void theGetByDriverIdMethodIsCalledForDriverWithId(long id) {
        try {
            rideListResponse = rideService.getByDriverId(id, FINISHED.toString(), VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
        } catch (InvalidRequestException e) {
            exception = e;
        }
    }

    @Then("Rides for driver is returned")
    public void driverSHistoryIsReturned() {
        assertEquals(rideListResponse.getRides().size(), 2);

    }
}
