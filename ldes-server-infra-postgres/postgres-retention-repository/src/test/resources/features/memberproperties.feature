Feature: MemberRepository
  As a user
  I want to interact with the MemberRepository to save and retrieve MemberProperties

  Scenario: Saving a MemberProperties with view
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               | viewReference |
      | http://test-data/mobility-hindrances/1/1 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 | by-page |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table

  Scenario: Adding a view to MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrances/1/1 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 |
    And I add the view with name "mobility-hindrances/by-page" to the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties has the view "mobility-hindrances/by-page" as a property

  Scenario: Retrieving MemberProperties with same VersionOf
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               | viewReference |
      | http://test-data/mobility-hindrances/1/1 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 | by-page |
      | http://test-data/mobility-hindrances/1/2 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 | by-page |
      | http://test-data/mobility-hindrances/2/1 | mobility-hindrances | http://test-data/mobility-hindrances/2 | 2023-07-05T15:28:49.665 | by-page |
      | http://test-data/mobility-hindrances/1/3 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 | by-page |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve all MemberProperties with versionOf "http://test-data/mobility-hindrances/1" from view "by-page"
    Then I have retrieved 3 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties contains MemberProperties 2 of the table
    And The retrieved MemberProperties contains MemberProperties 4 of the table

  Scenario: Retrieving MemberProperties with a view
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrances/1/1 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 |
      | http://test-data/mobility-hindrances/1/2 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 |
      | http://test-data/mobility-hindrances/1/3 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 |
    And I add the view with name "mobility-hindrances/by-page" to the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    And I add the view with name "mobility-hindrances/by-page" to the MemberProperties with id "http://test-data/mobility-hindrances/1/2"
    And I add the view with name "mobility-hindrances/by-version" to the MemberProperties with id "http://test-data/mobility-hindrances/1/2"
    And I add the view with name "mobility-hindrances/by-version" to the MemberProperties with id "http://test-data/mobility-hindrances/1/3"
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve all MemberProperties with view "mobility-hindrances/by-page"
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties contains MemberProperties 2 of the table
    And I retrieve all MemberProperties with view "mobility-hindrances/by-version"
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 2 of the table
    And The retrieved MemberProperties contains MemberProperties 3 of the table

  Scenario: Removing a view to MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrances/1/1 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 |
    And I add the view with name "mobility-hindrances/by-page" to the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties has the view "mobility-hindrances/by-page" as a property
    And I remove the view with name "mobility-hindrances/by-page" of the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And The retrieved MemberProperties does not have the view "mobility-hindrances/by-page" as a property

  Scenario: Deleting a MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrances/1/1 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And I delete the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    Then I have retrieved 0 MemberProperties

  Scenario: Removing a eventStream to MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp               |
      | http://test-data/mobility-hindrances/1/1 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties 1 of the table
    And I remove the eventStream with name "mobility-hindrances"
    And I retrieve the MemberProperties with id "http://test-data/mobility-hindrances/1/1"
    Then I have retrieved 0 MemberProperties

  Scenario: Retrieving expired MemberProperties by TimeBasedRetentionPolicy
    Given The following MemberProperties
      | id                                    | collectionName      | versionOf             | timestamp               | viewReference                   |
      | http://test-data/mh/expired-by-page/1 | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/expired-by-page/2 | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/future-timestamp  | mobility-hindrances | http://test-data/mh/1 | 3023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/other-view        | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-location |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the expired MemberProperties for "mobility-hindrances/by-page" with duration "P2D"
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/expired-by-page/1"
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/expired-by-page/2"
    When I retrieve the expired MemberProperties for "mobility-hindrances/by-page" with duration "P1000000D"
    Then I have retrieved 0 MemberProperties

  Scenario: Retrieving expired MemberProperties by VersionBasedRetentionPolicy
    Given The following MemberProperties
      | id                             | collectionName      | versionOf             | timestamp               | viewReference                   |
      | http://test-data/mh/1          | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/2          | mobility-hindrances | http://test-data/mh/1 | 2023-08-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/3          | mobility-hindrances | http://test-data/mh/1 | 2023-09-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/4          | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/other-view | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-location |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the expired MemberProperties for "mobility-hindrances/by-page" with 1 versions
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/1"
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/2"
    When I retrieve the expired MemberProperties for "mobility-hindrances/by-page" with 2 versions
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/1"
    When I retrieve the expired MemberProperties for "mobility-hindrances/by-page" with 3 versions
    Then I have retrieved 0 MemberProperties

  Scenario: Retrieving expired MemberProperties by TimeAndVersionBasedRetentionPolicy
    Given The following MemberProperties
      | id                             | collectionName      | versionOf             | timestamp               | viewReference                   |
      | http://test-data/mh/1          | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/2          | mobility-hindrances | http://test-data/mh/1 | 2023-08-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/3          | mobility-hindrances | http://test-data/mh/1 | 2023-09-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/4          | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/other-view | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-location |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the expired MemberProperties for "mobility-hindrances/by-page" with duration "P1000000D" and 1 versions
    Then I have retrieved 0 MemberProperties
    When I retrieve the expired MemberProperties for "mobility-hindrances/by-page" with duration "P1D" and 1 versions
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/1"
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/2"
    When I retrieve the expired MemberProperties for "mobility-hindrances/by-page" with duration "P1D" and 2 versions
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/1"

  Scenario: Adding a new view to the MemberProperties in bulk
    Given The following MemberProperties
      | id                         | collectionName      | versionOf             | timestamp               | viewReference               |
      | http://test-data/mh/1      | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page |
      | http://test-data/mh/2      | mobility-hindrances | http://test-data/mh/1 | 2023-08-05T15:28:49.665 | mobility-hindrances/by-page |
      | http://test-data/mh/3      | mobility-hindrances | http://test-data/mh/1 | 2023-09-05T15:28:49.665 | mobility-hindrances/by-page |
      | http://test-data/mh/4      | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page |
      | http://test-data/parcels/1 | parcels             | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | parcels/by-page             |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I bulk add the view with name "mobility-hindrances/by-location" to the MemberProperties
    And I retrieve all MemberProperties with view "mobility-hindrances/by-page"
    Then the MemberProperties all contain a reference to view "mobility-hindrances/by-location"
    When I retrieve all MemberProperties with view "parcels/by-page"
    Then the MemberProperties do not contain a reference to view "mobility-hindrances/by-location"

  Scenario: Retrieving expired MemberProperties for a collection by TimeBasedRetentionPolicy
    Given The following MemberProperties
      | id                                    | collectionName      | versionOf             | timestamp               | viewReference                   |
      | http://test-data/mh/expired-by-page/1 | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/expired-by-page/2 | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/future-timestamp  | mobility-hindrances | http://test-data/mh/1 | 3023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/other-view        | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-location |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the expired MemberProperties for collection "mobility-hindrances" with duration "P2D"
    Then I have retrieved 3 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/expired-by-page/1"
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/expired-by-page/2"
    When I retrieve the expired MemberProperties for collection "mobility-hindrances" with duration "P1000000D"
    Then I have retrieved 0 MemberProperties

  Scenario: Retrieving expired MemberProperties for a collection by VersionBasedRetentionPolicy
    Given The following MemberProperties
      | id                             | collectionName      | versionOf             | timestamp               | viewReference                   |
      | http://test-data/mh/1          | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/2          | mobility-hindrances | http://test-data/mh/1 | 2023-08-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/3          | mobility-hindrances | http://test-data/mh/1 | 2023-09-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/4          | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/other-view | mobility-hindrances | http://test-data/mh/3 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-location |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the expired MemberProperties for collection "mobility-hindrances" with 1 versions
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/1"
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/2"
    When I retrieve the expired MemberProperties for collection "mobility-hindrances" with 2 versions
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/1"
    When I retrieve the expired MemberProperties for collection "mobility-hindrances" with 3 versions
    Then I have retrieved 0 MemberProperties

  Scenario: Retrieving expired MemberProperties for a collection by TimeAndVersionBasedRetentionPolicy
    Given The following MemberProperties
      | id                             | collectionName      | versionOf             | timestamp               | viewReference                   |
      | http://test-data/mh/1          | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/2          | mobility-hindrances | http://test-data/mh/1 | 2023-08-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/3          | mobility-hindrances | http://test-data/mh/1 | 2023-09-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/4          | mobility-hindrances | http://test-data/mh/2 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page     |
      | http://test-data/mh/other-view | mobility-hindrances | http://test-data/mh/3 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-location |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I retrieve the expired MemberProperties for collection "mobility-hindrances" with duration "P1000000D" and 1 versions
    Then I have retrieved 0 MemberProperties
    When I retrieve the expired MemberProperties for collection "mobility-hindrances" with duration "P1D" and 1 versions
    Then I have retrieved 2 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/1"
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/2"
    When I retrieve the expired MemberProperties for collection "mobility-hindrances" with duration "P1D" and 2 versions
    Then I have retrieved 1 MemberProperties
    And The retrieved MemberProperties contains MemberProperties with id "http://test-data/mh/1"

  Scenario: Remove from event source
    Given The following MemberProperties
      | id                         | collectionName      | versionOf             | timestamp               | viewReference               | isInEventSource |
      | http://test-data/mh/1      | mobility-hindrances | http://test-data/mh/1 | 2023-07-05T15:28:49.665 | mobility-hindrances/by-page | true            |
      | http://test-data/mh/2      | mobility-hindrances | http://test-data/mh/1 | 2023-08-05T15:28:49.665 | mobility-hindrances/by-page | true            |
    When I save the MemberProperties using the MemberPropertiesRepository
    And I remove members "http://test-data/mh/1" and "http://test-data/mh/2" from the event source
    And I retrieve the expired MemberProperties for collection "mobility-hindrances" with duration "P1D"
    Then The MemberProperties are not in the event source