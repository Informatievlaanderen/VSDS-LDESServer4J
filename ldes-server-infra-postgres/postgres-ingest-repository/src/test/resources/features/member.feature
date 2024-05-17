Feature: MemberRepository
  As a user
  I want to interact with the MemberRepository to save, retrieve and delete Members

  Scenario: Saving a member with all attributes
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 15         | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
    Then The member with id "http://test-data/mobility-hindrance/1/1" can be retrieved from the database
    And The retrieved member has the same properties as the 1 member in the table and has sequenceNr 15
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Saving members without a sequenceNr which gets a sequenceNr assigned
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:23:05 |
      | http://test-data/gipod/1/1              | gipod               | [blank]    | http://test-data/gipod/1              | 2022-08-12T18:00:00 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:48:35 |
      | http://test-data/gipod/1/2              | gipod               | [blank]    | http://test-data/gipod/1              | 2022-08-12T18:48:00 |
    Then The member with id "http://test-data/mobility-hindrance/1/1" can be retrieved from the database
    And The retrieved member has the same properties as the 1 member in the table and has sequenceNr 1
    Then The member with id "http://test-data/mobility-hindrance/1/2" can be retrieved from the database
    And The retrieved member has the same properties as the 2 member in the table and has sequenceNr 2
    Then The member with id "http://test-data/mobility-hindrance/1/3" can be retrieved from the database
    And The retrieved member has the same properties as the 4 member in the table and has sequenceNr 3
    Then The member with id "http://test-data/gipod/1/1" can be retrieved from the database
    And The retrieved member has the same properties as the 3 member in the table and has sequenceNr 1
    Then The member with id "http://test-data/gipod/1/2" can be retrieved from the database
    And The retrieved member has the same properties as the 5 member in the table and has sequenceNr 2
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "gipod" collection

  Scenario: The repository can delete all members of a certain collection
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:23:05 |
      | http://test-data/gipod/1/1              | gipod               | [blank]    | http://test-data/gipod/1              | 2022-08-12T18:00:00 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:48:35 |
      | http://test-data/gipod/1/2              | gipod               | [blank]    | http://test-data/gipod/1              | 2022-08-12T18:48:00 |
    Then The member with id "http://test-data/gipod/1/1" will exist
    When I delete all the members of collection "gipod"
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    And The member with id "http://test-data/gipod/1/1" will not exist
    And The member with id "http://test-data/gipod/1/2" will not exist
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "gipod" collection

  Scenario: The repository can indicate if members exist or not
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    And The member with id "http://test-data/mobility-hindrance/fantasy-id" will not exist
    And I delete all members from the "mobility-hindrances" collection

  Scenario: The repository can provide a stream of the eventsource
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 0          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | 1          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:23:05 |
      | http://test-data/gipod/1/1              | gipod               | 0          | http://test-data/gipod/1              | 2022-08-12T18:00:00 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | 2          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:48:35 |
      | http://test-data/gipod/1/2              | gipod               | 1          | http://test-data/gipod/1              | 2022-08-12T18:48:00 |
    Then I can get an ordered stream from all the members of the "mobility-hindrances" collection containing 3 members
    And I can get an ordered stream from all the members of the "gipod" collection containing 2 members
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "gipod" collection

  Scenario: The repository can bulk retrieve members
    Given I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 0          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | 1          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:23:05 |
      | http://test-data/gipod/1/1              | gipod               | 0          | http://test-data/gipod/1              | 2022-08-12T18:00:00 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | 2          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:48:35 |
      | http://test-data/gipod/1/2              | gipod               | 1          | http://test-data/gipod/1              | 2022-08-12T18:48:00 |
    When I try to retrieve the following members by Id
      | http://test-data/mobility-hindrance/1/1 | http://test-data/mobility-hindrance/1/2 | http://test-data/mobility-hindrance/1/3 |
    Then I expect a list of 3 members
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "gipod" collection

  Scenario: The repository can indicate if members exist or not
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    And The member with id "http://test-data/mobility-hindrance/fantasy-id" will not exist
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Delete a member with a certain id
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:35:00 |
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    When I delete the member with id "http://test-data/mobility-hindrance/1/1"
    Then The member with id "http://test-data/mobility-hindrance/1/2" will exist
    And The member with id "http://test-data/mobility-hindrance/1/1" will not exist
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Find member by collection name and sequence number greater than
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 5          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | 12         | http://test-data/mobility-hindrance/1 | 2022-08-12T18:35:00 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | 3          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:55:00 |
    And I search for the first member from collection "mobility-hindrances" and sequenceNr greater than -1
    Then The retrieved member has the same properties as the 3 member in the table and has sequenceNr 3
    When I search for the first member from collection "mobility-hindrances" and sequenceNr greater than 3
    Then The retrieved member has the same properties as the 1 member in the table and has sequenceNr 5
    When I search for the first member from collection "mobility-hindrances" and sequenceNr greater than 15
    Then The retrieved member is empty
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Get current number of members
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 5          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | 12         | http://test-data/mobility-hindrance/1 | 2022-08-12T18:35:00 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | 3          | http://test-data/mobility-hindrance/1 | 2022-08-12T18:55:00 |
      | http://test-data/other/1/1              | other               | 4          | http://test-data/mobility-hindrance/1 | 2022-08-12T19:26:00 |
    Then The number of members is 4
    And The number of members of the "mobility-hindrances" collection is 3
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "other" collection

  Scenario: Members with the same id are not bulk inserted in the MemberRepository
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/2 | 2022-08-12T18:35:00 |
    Then The member with id "http://test-data/mobility-hindrance/1/1" will not exist
    And The number of members of the "mobility-hindrances" collection is 0
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Members with the same id are not inserted in the MemberRepository
    When I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
    And I save the members using the MemberRepository
      | id                                      | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/2 | 2022-08-12T18:35:00 |
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    And The number of members of the "mobility-hindrances" collection is 1
    And I delete all members from the "mobility-hindrances" collection