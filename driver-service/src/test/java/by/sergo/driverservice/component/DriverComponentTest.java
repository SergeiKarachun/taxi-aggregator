package by.sergo.driverservice.component;


import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.kafka.producer.DriverProducer;
import by.sergo.driverservice.mapper.DriverMapper;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.NotFoundException;
import by.sergo.driverservice.service.impl.DriverServiceImpl;
import by.sergo.driverservice.util.DriverTestUtils;
import by.sergo.driverservice.util.ExceptionMessageUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;


import static by.sergo.driverservice.util.DriverTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@CucumberContextConfiguration
public class DriverComponentTest {
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private DriverMapper driverMapper;
    @Mock
    private DriverProducer driverProducer;
    @InjectMocks
    private DriverServiceImpl driverService;

    private DriverResponse driverResponse;
    private Exception exception;


    @Given("A driver with id {long} exists")
    public void passengerWithIdExists(long id) {
        Driver driver = getDefaultDriver();
        DriverResponse expected = getDefaultDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(id);
        doReturn(expected)
                .when(driverMapper)
                .mapToDto(driver);


        DriverResponse actual = driverService.getById(id);
        assertThat(actual).isEqualTo(expected);
    }

    @When("The id {long} is passed to the findById method")
    public void theIdIsPassedToTheFindByIdMethod(long id) {
        try {
            driverResponse = driverService.getById(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain driver with id {long}")
    public void theResponseShouldContainDriverWithId(long id) {
        Driver driver = driverRepository.findById(id).get();
        DriverResponse expected = driverMapper.mapToDto(driver);

        assertThat(driverResponse).isEqualTo(expected);
    }

    @Given("A driver with id {long} doesn't exist")
    public void aDriverWithIdDoesnTExist(long id) {
        Optional<Driver> driver = driverRepository.findById(id);
        assertFalse(driver.isPresent());
    }

    @Then("The NotFoundException with id {long} should be thrown")
    public void theNotFoundExceptionWithIdShouldBeThrown(long id) {
        String expected = ExceptionMessageUtil.getNotFoundMessage("Driver", "id", id);
        String actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @When("The id {long} is passed to the deleteById method")
    public void theIdIsPassedToTheDeleteByIdMethod(long id) {
        try {
            //var driver = driverRepository.findById(id).get();
            driverResponse = driverService.delete(id);
            //driverResponse = driverMapper.mapToDto(driver);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain message with id {long}")
    public void theResponseShouldContainMessageWithId(long id) {
        Driver driver = driverRepository.findById(id).get();
        DriverResponse expected = driverMapper.mapToDto(driver);

        assertThat(driverResponse).isEqualTo(expected);
    }

    @Given("A driver with phone {string} exists")
    public void aDriverWithPhoneExists(String phone) {
        DriverResponse expected = getDefaultDriverResponse();
        Driver driverToSave = getNotSavedDriver();
        Driver savedDriver = getDefaultDriver();
        doReturn(Optional.of(getDefaultDriver()))
                .when(driverRepository)
                .findById(anyLong());
        doReturn(true)
                .when(driverRepository)
                .existsByPhone(phone);
        doReturn(driverToSave)
                .when(driverMapper)
                .mapToEntity(any(DriverCreateUpdateRequest.class));
        doReturn(savedDriver)
                .when(driverRepository)
                .save(driverToSave);
        doReturn(expected)
                .when(driverMapper)
                .mapToDto(any(Driver.class));

        assertTrue(driverRepository.existsByPhone(phone));
    }

    @When("A create request with phone {string} is passed to the add method")
    public void aCreateRequestWithPhoneIsPassedToTheAddMethod(String phone) {
        DriverCreateUpdateRequest createRequest = DriverTestUtils.getDefaultDriverRequest();
        createRequest.setPhone(phone);
        try {
            driverResponse = driverService.create(createRequest);
        } catch (NotFoundException | BadRequestException e) {
            exception = e;
        }
    }

    @Then("The AlreadyExistsException should be thrown for phone {string}")
    public void theAlreadyExistsExceptionShouldBeThrownForPhone(String phone) {
        assertEquals(exception.getMessage(), ExceptionMessageUtil.getAlreadyExistMessage("Driver", "phone", phone));
    }

    /*@Given("A driver with phone {string} doesn't exist")
    public void aDriverWithPhoneDoesnTExist(String arg0) {
    }

    @Then("The response should contain created driver")
    public void theResponseShouldContainCreatedDriver() {
    }

    @When("An update request with phone {string} for driver with id {int} is passed to the update method")
    public void anUpdateRequestWithPhoneForDriverWithIdIsPassedToTheUpdateMethod(String arg0, int arg1) {
    }

    @Given("A driver with id {int} exists when phone {string} doesn't exist")
    public void aDriverWithIdExistsWhenPhoneDoesnTExist(int arg0, String arg1) {
    }

    @Then("The response should contain updated driver with id {int}")
    public void theResponseShouldContainUpdatedDriverWithId(int arg0) {
    }

    @When("Driver id {int} is passed to the changeStatus method")
    public void driverIdIsPassedToTheChangeStatusMethod(int arg0) {
    }

    @Then("The response should contain status message with id {int}")
    public void theResponseShouldContainStatusMessageWithId(int arg0) {
    }

    @Given("A list of available drivers")
    public void aListOfAvailableDrivers() {
    }

    @When("The findAvailableDrivers method is called with valid parameters")
    public void theFindAvailableDriversMethodIsCalledWithValidParameters() {
    }

    @Then("A list of drivers is returned")
    public void aListOfDriversIsReturned() {
    }*/
}
