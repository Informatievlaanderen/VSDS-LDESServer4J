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

  Scenario: The repository can delete all members of a certain view
    Given The following members
      | id                                      | collectionName      | sequenceNr | versionOf                             |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 |
    And The following treeNodeReferences on the members
      | /mobility-hindrance/by-page?pageNumber=1   |
      | /mobility-hindrance/by-page?pageNumber=1   |
      | /mobility-hindrance/by-location?tile=0/0/0 |
    Then I save the members using the MemberRepository
    When I remove the view references of view 'mobility-hindrance/by-page'
    Then The members of collection "mobility-hindrances" will only have treeNodeReference "/mobility-hindrance/by-location?tile=0/0/0"
