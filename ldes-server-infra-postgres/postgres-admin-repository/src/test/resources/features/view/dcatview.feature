Feature: DcatViewRepository
  As a user
  I want to interact with the DcatViewRepository to handle dataservices

  Scenario: Create Read Update and Delete a dataservice with all attributes
    Given I have an eventstream with a view
    And I have a dcatView with a viewName and model
    Then I can save the dcatView with the repository
    And I can retrieve the dcatView from the repository
    And The retrieved dcatView will be the same as the saved dcatView
    Then I can update the dcatView with a new model
    And I can retrieve the dcatView from the repository
    And The retrieved dcatView will be the same as the saved dcatView
    Then I can delete the dcatView
    And I can not retrieve the dcatView from the repository

  Scenario: I can find all views
    Given I have an eventstream with a view
    And the database contains multiple dcatViews
    Then I can find all dcatViews

  Scenario: A dcatView is deleted when the eventstream is deleted
    Given I have an eventstream with a view
    And I have a dcatView with a viewName and model
    And the database already contains another dcatView
    When I can save the dcatView with the repository
    Then the repository contains exactly 2 dcatViews
    When I delete the corresponding eventstream
    Then the repository contains exactly 1 dcatViews
