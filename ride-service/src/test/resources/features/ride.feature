Feature: Ride Service

  Scenario: Retrieving a ride by existing id
    Given A ride with id "65afd8b6759a765221df8051" exists
    When The id "65afd8b6759a765221df8051" is passed to the findById method
    Then The response should contain ride with id "65afd8b6759a765221df8051"

  Scenario: Retrieving a ride by non-existing id
    Given A ride with id "65afd8b6759a765221df8051" doesn't exist
    When The id "65afd8b6759a765221df8051" is passed to the findById method
    Then The NotFoundException with id "65afd8b6759a765221df8051" should be thrown

  Scenario: Deleting a ride by non-existing id
    Given A ride with id "65afd8b6759a765221df8051" doesn't exist
    When The id "65afd8b6759a765221df8051" is passed to the deleteById method
    Then The NotFoundException with id "65afd8b6759a765221df8051" should be thrown

  Scenario: Deleting a ride by existing id
    Given A ride with id "65afd8b6759a765221df8051" exists
    When The id "65afd8b6759a765221df8051" is passed to the deleteById method
    Then The response should contain response with id "65afd8b6759a765221df8051"

  Scenario: Creating a new ride
    Given Create ride with default data
    When A create request is passed to the add method
    Then The response should contain created ride

  Scenario: Update ride by non-existing id
    Given A ride with id "65afd8b6759a765221df8051" doesn't exist
    When An update request for ride with id "65afd8b6759a765221df8051" is passed to the update method
    Then The NotFoundException with id "65afd8b6759a765221df8051" should be thrown

  Scenario: Update ride
    Given A ride with id "65afd8b6759a765221df8051" for update exists
    When An update request for ride with id "65afd8b6759a765221df8051" is passed to the update method
    Then The response should contain updated ride with id "65afd8b6759a765221df8051"

  Scenario: Change ride status by non-existing id
    Given A ride with id "65afd8b6759a765221df8051" doesn't exist
    When Ride id "65afd8b6759a765221df8051" is passed to the rejectRide method
    Then The NotFoundException with id "65afd8b6759a765221df8051" should be thrown

  Scenario: Change ride status by existing id
    Given A ride with id "65afd8b6759a765221df8051" for editing status exists
    When Ride id "65afd8b6759a765221df8051" is passed to the rejectRide method
    Then The response should contain updated ride with id "65afd8b6759a765221df8051"

  Scenario: Change ride status by existing id when ride status not accepted
    Given A ride with id "65afd8b6759a765221df8051" for editing status exists when status created
    When Ride id "65afd8b6759a765221df8051" is passed to the startRide method
    Then The BadRequestException should be thrown

  Scenario: Find all rides for passenger
    Given History of rides for passenger with id 1
    When The getByPassengerId method is called for passenger with id 1
    Then Rides for passenger is returned

  Scenario: Find all rides for driver
    Given History of rides for driver with id 1
    When The getByDriverId method is called for driver with id 1
    Then Rides for driver is returned