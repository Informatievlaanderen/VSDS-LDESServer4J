Feature: MemberRepository
  As a user
  I want to interact with the MemberRepository to save and retrieve MemberProperties

  Scenario: Saving a MemberProperties
    Given The following MemberProperties
      | id                                      | collectionName      | versionOf                             | timestamp                     |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2023-07-05T15:28:49.665 |
    When I save the MemberProperties using the MemberPropertiesRepository
    Then The MemberProperties with id "http://test-data/mobility-hindrance/1/1" can be retrieved from the database
    And The retrieved MemberProperties has the same properties as the 1 MemberProperties in the table