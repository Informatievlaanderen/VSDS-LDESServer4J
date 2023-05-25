Feature: ServerDcatRepository
  As a data publisher
  I want to interact with the ServerDcatRepository to handle serverdcats

  Scenario: Create Read Update and Deelete a ServerDcat with all attributes
    Given I have a serverDcat with an id and model
    Then I can save the serverDcat with the repository
    And I can retrieve the serverDcat from the repository
    And The retrieved serverDcat will be the same as the saved serverDcat
    Then I can update the serverDcat with a new model
    And I can retrieve the serverDcat from the repository
    And The retrieved serverDcat will be the same as the saved serverDcat
    Then I can delete the serverDcat
    And I can not retrieve the serverDcat from the repository

