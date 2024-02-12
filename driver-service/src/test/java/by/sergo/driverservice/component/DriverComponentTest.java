package by.sergo.driverservice.component;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.DriverListResponse;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.domain.enums.Status;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
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
    private DriverListResponse driverListResponse;
    private Exception exception;

    @Given("A driver with id {long} exists")
    public void passengerWithIdExists(long id) {
        Driver driver = getDefaultDriver();
        DriverResponse expected = getDefaultDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(id);
        driver.setStatus(Status.UNAVAILABLE);
        doReturn(driver)
                .when(driverRepository)
                .save(driver);
        expected.setStatus(Status.UNAVAILABLE.name());
        doReturn(expected)
                .when(driverMapper)
                .mapToDto(any());


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
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(id);
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

    @Then("The response should contain response with id {long}")
    public void theResponseShouldContainMessageWithId(long id) {
        Driver driver = driverRepository.findById(id).get();
        DriverResponse expected = driverMapper.mapToDto(driver);

        assertEquals(expected.getId(), id);
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

    @Given("A driver with phone {string} doesn't exist")
    public void aDriverWithPhoneDoesnTExist(String phone) {
        DriverResponse expected = getDefaultDriverResponse();
        Driver driverToSave = getNotSavedDriver();
        Driver savedDriver = getDefaultDriver();
        doReturn(Optional.of(getDefaultDriver()))
                .when(driverRepository)
                .findById(anyLong());
        doReturn(false)
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

        assertFalse(driverRepository.existsByPhone(phone));
    }

    @Then("The response should contain created driver")
    public void theResponseShouldContainCreatedDriver() {
        var expected = getDefaultDriverResponse();
        assertEquals(driverResponse, expected);
    }

    @When("An update request with phone {string} for driver with id {long} is passed to the update method")
    public void anUpdateRequestWithPhoneForDriverWithIdIsPassedToTheUpdateMethod(String phone, long id) {
        var request = DriverTestUtils.getDriverRequest(phone);
        try {
            driverResponse = driverService.update(id, request);
        } catch (NotFoundException | BadRequestException e) {
            exception = e;
        }
    }

    @Given("A driver with id {long} exists when phone {string} doesn't exist")
    public void aDriverWithIdExistsWhenPhoneDoesnTExist(long id, String phone) {
        Driver driverToUpdate = getUpdateDriver(phone);
        DriverResponse notSavedDriver = getNotSavedResponse(phone);
        Driver savedDriver = getSavedDriver(id, phone);
        doReturn(Optional.of(driverToUpdate))
                .when(driverRepository)
                .findById(id);
        doReturn(Optional.of(driverToUpdate))
                .when(driverRepository)
                .findById(id);
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(phone);
        doReturn(driverToUpdate)
                .when(driverMapper)
                .mapToEntity(any(DriverCreateUpdateRequest.class));
        doReturn(savedDriver)
                .when(driverRepository)
                .save(any(Driver.class));
        notSavedDriver.setId(id);
        doReturn(notSavedDriver)
                .when(driverMapper)
                .mapToDto(any(Driver.class));
    }

    @Then("The response should contain updated driver with id {long}")
    public void theResponseShouldContainUpdatedDriverWithId(long id) {
        DriverResponse actual = driverMapper.mapToDto(driverRepository.findById(id).get());

        assertEquals(actual, driverResponse);
    }

    @When("Driver id {long} is passed to the changeStatus method")
    public void driverIdIsPassedToTheChangeStatusMethod(long id) {
        try {
            driverResponse = driverService.changeStatus(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain status message with id {long}")
    public void theResponseShouldContainStatusMessageWithId(long id) {
        DriverResponse actual = DriverResponse.builder()
                .id(id)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .status(Status.UNAVAILABLE.name())
                .build();

        assertEquals(actual, driverResponse);
    }

    @Given("A list of available drivers")
    public void aListOfAvailableDrivers() {
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(getDefaultDriver(), getSecondDriver()));
        doReturn(Optional.of(getDefaultDriver()))
                .when(driverRepository)
                .findById(anyLong());
        doReturn(driverPage)
                .when(driverRepository)
                .getAllByStatus(any(Status.class), any(PageRequest.class));
        doReturn(getDefaultDriverResponse())
                .when(driverMapper)
                .mapToDto(any(Driver.class));
    }

    @When("The findAvailableDrivers method is called with valid parameters")
    public void theFindAvailableDriversMethodIsCalledWithValidParameters() {
        driverListResponse = driverService.getAvailableDrivers(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
    }

    @Then("A list of drivers is returned")
    public void aListOfDriversIsReturned() {
        assertEquals(driverListResponse.getDrivers().size(), 2);
    }
}
