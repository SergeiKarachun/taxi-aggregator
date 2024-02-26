package by.sergo.passengerservice.component;

import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerResponse;
import by.sergo.passengerservice.domain.entity.Passenger;
import by.sergo.passengerservice.mapper.PassengerMapper;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.service.exception.BadRequestException;
import by.sergo.passengerservice.service.exception.NotFoundException;
import by.sergo.passengerservice.service.impl.PassengerServiceImpl;
import by.sergo.passengerservice.util.ExceptionMessageUtil;
import by.sergo.passengerservice.util.PassengerTestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static by.sergo.passengerservice.util.PassengerTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@CucumberContextConfiguration
public class PassengerComponentTest {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private PassengerMapper passengerMapper;
    @InjectMocks
    private PassengerServiceImpl passengerService;

    private PassengerResponse passengerResponse;
    private Exception exception;


    @Given("A passenger with id {long} exists")
    public void passengerWithIdExists(long id) {
        Passenger existPassenger = getDefaultPassenger();
        PassengerResponse expectedResponse = getDefaultPassengerResponse();

        doReturn(Optional.of(existPassenger))
                .when(passengerRepository)
                .findById(id);
        doReturn(expectedResponse)
                .when(passengerMapper)
                .mapToDto(any(Passenger.class));

        PassengerResponse actual = passengerService.getById(id);
        assertEquals(expectedResponse, actual);
    }

    @When("The id {long} is passed to the findById method")
    public void theIdIsPassedToTheFindByIdMethod(long id) {
        try {
            passengerResponse = passengerService.getById(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain passenger with id {long}")
    public void theResponseShouldContainPassengerWithId(long id) {
        Passenger passenger = passengerRepository.findById(id).get();
        PassengerResponse expected = passengerMapper.mapToDto(passenger);

        assertThat(passengerResponse).isEqualTo(expected);
    }

    @Given("A passenger with id {long} doesn't exist")
    public void aPassengerWithIdDoesnTExist(long id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        assertFalse(passenger.isPresent());
    }

    @Then("The NotFoundException with id {long} should be thrown")
    public void theNotFoundExceptionWithIdShouldBeThrown(long id) {
        String expected = ExceptionMessageUtil.getNotFoundMessage("Passenger", "id", id);
        String actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @When("The id {long} is passed to the deleteById method")
    public void theIdIsPassedToTheDeleteByIdMethod(long id) {
        try {
            passengerResponse = passengerService.delete(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain response with id {long}")
    public void theResponseShouldContainMessageWithId(long id) {
        Passenger passenger = passengerRepository.findById(id).get();
        PassengerResponse expected = passengerMapper.mapToDto(passenger);

        assertEquals(expected.getId(), id);
        assertThat(passengerResponse).isEqualTo(expected);
    }

    @Given("A passenger with email {string} and phone {string} doesn't exist")
    public void aPassengerWithEmailAndPhoneDoesnTExist(String email, String phone) {
        PassengerResponse expected = getDefaultPassengerResponse();
        Passenger passengerToSave = getPassengerToSave();
        Passenger savedPassenger = getDefaultPassenger();
        PassengerCreateUpdateRequest createRequest = getPassengerRequest();

        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(email);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(phone);
        doReturn(passengerToSave)
                .when(passengerMapper)
                .mapToEntity(any(PassengerCreateUpdateRequest.class));
        doReturn(savedPassenger)
                .when(passengerRepository)
                .save(passengerToSave);
        doReturn(expected)
                .when(passengerMapper)
                .mapToDto(any(Passenger.class));

        PassengerResponse actual = passengerService.create(createRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @When("A create request with email {string}, phone {string} is passed to the add method")
    public void aCreateRequestWithEmailPhoneIsPassedToTheAddMethod(String email, String phone) {
        PassengerCreateUpdateRequest createRequest = getPassengerRequest(email, phone);
        try {
            passengerResponse = passengerService.create(createRequest);
        } catch (BadRequestException e) {
            exception = e;
        }
    }

    @Then("The response should contain created passenger")
    public void theResponseShouldContainCreatedPassenger() {
        var expected = getDefaultPassengerResponse();
        assertThat(passengerResponse).isEqualTo(expected);
    }

    @Given("A passenger with email {string} exists")
    public void aPassengerWithEmailExists(String email) {
        doReturn(true)
                .when(passengerRepository)
                .existsByEmail(email);

        assertTrue(passengerRepository.existsByEmail(email));
    }

    @Then("The BadRequestException for email should be thrown")
    public void theBadRequestExceptionForEmailShouldBeThrown() {
        String expected = "Passenger with this email petr@gmail.com already exists..";
        String actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @Given("A passenger with phone {string} exists")
    public void aPassengerWithPhoneExists(String phone) {
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(phone);

        assertTrue(passengerRepository.existsByPhone(phone));
    }

    @Then("The BadRequestException for phone should be thrown")
    public void theBadRequestExceptionForPhoneShouldBeThrown() {
        String expected = "Passenger with this phone +375331234567 already exists..";
        String actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @When("An update request with email {string}, phone {string} for passenger with id {long} is passed to the update method")
    public void anUpdateRequestWithEmailPhoneForPassengerWithIdIsPassedToTheUpdateMethod(String email, String phone, long id) {
        try {
            passengerResponse = passengerService.update(id, getPassengerRequest(email, phone));
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Given("A passenger with id {long} exists when email {string} and phone {string} doesn't exist")
    public void aPassengerWithIdExistsWhenEmailAndPhoneDoesnTExist(long id, String email, String phone) {
        Passenger passengerToUpdate = PassengerTestUtils.getUpdatePassenger(email, phone);
        PassengerResponse notSavedPassenger = getUpdateResponse(email, phone);
        doReturn(Optional.of(passengerToUpdate))
                .when(passengerRepository)
                .findById(id);
        doReturn(Optional.of(passengerToUpdate))
                .when(passengerRepository)
                .findById(id);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(phone);
        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(email);
        doReturn(passengerToUpdate)
                .when(passengerMapper)
                .mapToEntity(any(PassengerCreateUpdateRequest.class));
        passengerToUpdate.setId(id);
        doReturn(passengerToUpdate)
                .when(passengerRepository)
                .save(any(Passenger.class));
        notSavedPassenger.setId(id);
        doReturn(notSavedPassenger)
                .when(passengerMapper)
                .mapToDto(any(Passenger.class));
    }

    @Then("The response should contain updated passenger with id {long}")
    public void theResponseShouldContainUpdatedPassengerWithId(long id) {
        PassengerResponse actual = passengerMapper.mapToDto(passengerRepository.findById(id).get());
        assertThat(actual).isEqualTo(passengerResponse);
    }
}
