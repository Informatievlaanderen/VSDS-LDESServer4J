Feature: MemberRepository
  As a user
  I want to interact with the MemberRepository to save and retrieve MemberProperties

  Scenario: Saving a MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table

  Scenario: Adding a view to MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I add the view with name "mobility-hindrance/by-page" to the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties has the view "mobility-hindrance/by-page" as a property

  Scenario: Retrieving MemberProperties with same VersionOf
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
      | http://test-data/mobility-hindrance/2/1 | mobility-hindrances | http://test-data/mobility-hindrance/2 | 2023-07-05T15:28:49.665 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve all MemberProperties with versionOf "http://test-data/mobility-hindrance/1"
    Then I have retrieved 3 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties contains MemberProperties 2 of the table
    And The retrieved MemberProperties contains MemberProperties 4 of the table

  Scenario: Retrieving MemberProperties with a view
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I add the view with name "mobility-hindrance/by-page" to the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    And I add the view with name "mobility-hindrance/by-page" to the MemberProperties with id "http://test-data/mobility-hindrance/1/2"
    And I add the view with name "mobility-hindrance/by-version" to the MemberProperties with id "http://test-data/mobility-hindrance/1/2"
    And I add the view with name "mobility-hindrance/by-version" to the MemberProperties with id "http://test-data/mobility-hindrance/1/3"
    And I retrieve all MemberProperties with view "mobility-hindrance/by-page"
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties contains MemberProperties 2 of the table
    And I retrieve all MemberProperties with view "mobility-hindrance/by-version"
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 2 of the table
    And The retrieved MemberProperties contains MemberProperties 3 of the table

  Scenario: Removing a view to MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I add the view with name "mobility-hindrance/by-page" to the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties has the view "mobility-hindrance/by-page" as a property
    And I remove the view with name "mobility-hindrance/by-page" of the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties does not have the view "mobility-hindrance/by-page" as a property

  Scenario: Deleting a MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And I delete the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrance/1/1"
    Then I have retrieved 0 MemberProperties