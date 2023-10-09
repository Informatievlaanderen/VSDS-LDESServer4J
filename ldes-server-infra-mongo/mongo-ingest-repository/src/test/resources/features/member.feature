Feature: MemberRepository
  As a user
  I want to interact with the MemberRepository to save, retrieve and delete Members

  Scenario: Saving a member with all attributes
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 15         | http://test-data/mobility-hindrance/1 |
    When I save the members using the MemberRepository
    Then The member with id "http://test-data/mobility-hindrance/1/1" can be retrieved from the database
    And The retrieved member has the same properties as the 1 member in the table and has sequenceNr 15
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Saving members without a sequenceNr which gets a sequenceNr assigned
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/gipod/1/1              | gipod               | [blank]    | http://test-data/gipod/1              |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/gipod/1/2              | gipod               | [blank]    | http://test-data/gipod/1              |
    When I save the members using the MemberRepository
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
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/gipod/1/1              | gipod               | [blank]    | http://test-data/gipod/1              |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/gipod/1/2              | gipod               | [blank]    | http://test-data/gipod/1              |
    When I save the members using the MemberRepository
    Then The member with id "http://test-data/gipod/1/1" will exist
    When I delete all the members of collection "gipod"
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    And The member with id "http://test-data/gipod/1/1" will not exist
    And The member with id "http://test-data/gipod/1/2" will not exist
    And The sequence for "gipod" will have been removed
    And The sequence for "mobility-hindrances" will still exist
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "gipod" collection

  Scenario: The repository can indicate if members exist or not
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
    When I save the members using the MemberRepository
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    And The member with id "http://test-data/mobility-hindrance/fantasy-id" will not exist
    And I delete all members from the "mobility-hindrances" collection

  Scenario: The repository can provide a stream of the eventsource
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 0          | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | 1          | http://test-data/mobility-hindrance/1 |
      | http://test-data/gipod/1/1              | gipod               | 0          | http://test-data/gipod/1              |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | 2          | http://test-data/mobility-hindrance/1 |
      | http://test-data/gipod/1/2              | gipod               | 1          | http://test-data/gipod/1              |
    When I save the members using the MemberRepository
    Then I can get an ordered stream from all the members of the "mobility-hindrances" collection containing 3 members
    And I can get an ordered stream from all the members of the "gipod" collection containing 2 members
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "gipod" collection

  Scenario: The repository can bulk retrieve members
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 0          | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | 1          | http://test-data/mobility-hindrance/1 |
      | http://test-data/gipod/1/1              | gipod               | 0          | http://test-data/gipod/1              |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | 2          | http://test-data/mobility-hindrance/1 |
      | http://test-data/gipod/1/2              | gipod               | 1          | http://test-data/gipod/1              |
    And I save the members using the MemberRepository
    When I try to retrieve the following members by Id
      | http://test-data/mobility-hindrance/1/1 | http://test-data/mobility-hindrance/1/2 | http://test-data/mobility-hindrance/1/3 |
    Then I expect a list of 3 members
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "gipod" collection

  Scenario: The repository can indicate if members exist or not
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
    When I save the members using the MemberRepository
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    And The member with id "http://test-data/mobility-hindrance/fantasy-id" will not exist
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Delete a member with a certain id
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
    When I save the members using the MemberRepository
    Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
    When I delete the member with id "http://test-data/mobility-hindrance/1/1"
    Then The member with id "http://test-data/mobility-hindrance/1/2" will exist
    And The member with id "http://test-data/mobility-hindrance/1/1" will not exist
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Find member by collection name and sequence number greater than
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 5          | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | 12         | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | 3          | http://test-data/mobility-hindrance/1 |
    When I save the members using the MemberRepository
    And I search for the first member from collection "mobility-hindrances" and sequenceNr greater than -1
    Then The retrieved member has the same properties as the 3 member in the table and has sequenceNr 3
    When I search for the first member from collection "mobility-hindrances" and sequenceNr greater than 3
    Then The retrieved member has the same properties as the 1 member in the table and has sequenceNr 5
    When I search for the first member from collection "mobility-hindrances" and sequenceNr greater than 15
    Then The retrieved member is empty
    And I delete all members from the "mobility-hindrances" collection

  Scenario: Get current number of members
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 5          | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | 12         | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | 3          | http://test-data/mobility-hindrance/1 |
      | http://test-data/other/1/1              | other               | 4          | http://test-data/mobility-hindrance/1 |
    When I save the members using the MemberRepository
    Then The number of members is 4
    And The number of members of the "mobility-hindrances" collection is 3
    And I delete all members from the "mobility-hindrances" collection
    And I delete all members from the "other" collection