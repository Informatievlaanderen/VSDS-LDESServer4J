Feature: MemberRepository
  As a user
  I want to interact with the MemberRepository to save and retrieve Members

  Scenario: Saving a member with all attributes
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | 15         | http://test-data/mobility-hindrance/1 |
    When I save the members using the MemberRepository
    Then The member with id "http://test-data/mobility-hindrance/1/1" can be retrieved from the database
    And The retrieved member has the same properties as the 1 member in the table and has sequenceNr 15

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

    Scenario: The repository can indicate if members exist or not
      Given The following members
        | id                                      | collectionName      | sequenceNr | versionOf                             |
        | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      When I save the members using the MemberRepository
      Then The member with id "http://test-data/mobility-hindrance/1/1" will exist
      And Then The member with id "http://test-data/mobility-hindrance/fantasy-id" will not exist