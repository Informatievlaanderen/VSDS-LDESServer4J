Feature: DcatViewRepository
  As a user
  I want to interact with the DcatViewRepository to handle dataservices

  Scenario: Create Read Update and Delete a dataservice with all attributes
    Given I have a dcatView with a viewName and model
    Then I can save the dcatView with the repository
    And I can retrieve the dcatView from the repository
    And The retrieved dcatView will be the same as the saved dcatView
    Then I can update the dcatView with a new model
    And I can retrieve the dcatView from the repository
    And The retrieved dcatView will be the same as the saved dcatView
    Then I can delete the dcatView
    And I can not retrieve the dcatView from the repository

