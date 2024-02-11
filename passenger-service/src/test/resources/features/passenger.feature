Feature: Passenger Service

  Scenario: Getting a passenger by existing id
    Given A passenger with id 1 exists
    When The id 1 is passed to the findById method
    Then The response should contain passenger with id 1

  Scenario: Getting a passenger by non-existing id
    Given A passenger with id 1 doesn't exist
    When The id 1 is passed to the findById method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Deleting a passenger by non-existing id
    Given A passenger with id 1 doesn't exist
    When The id 1 is passed to the deleteById method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Deleting a passenger by existing id
    Given A passenger with id 1 exists
    When The id 1 is passed to the deleteById method
    Then The response should contain message with id 1

  Scenario: Creating a new passenger with unique data
    Given A passenger with email "petr@gmail.com" and phone "+375331234567" doesn't exist
    When A create request with email "petr@gmail.com", phone "+375331234567" is passed to the add method
    Then The response should contain created passenger

  Scenario: Creating a new passenger with non-unique phone
    Given A passenger with phone "+375331234567" exists
    When A create request with email "petr@gmail.com", phone "+375331234567" is passed to the add method
    Then The BadRequestException for phone should be thrown

  Scenario: Update passenger by non-existing id
    Given A passenger with id 1 doesn't exist
    When An update request with email "petr@gmail.com", phone "+375331234567" for passenger with id 1 is passed to the update method
    Then The NotFoundException with id 1 should be thrown
