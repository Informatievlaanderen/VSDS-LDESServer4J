Feature: MemberRepository
  As a user
  I want to interact with the MemberRepository to save, retrieve and delete Members

  Scenario: Saving a member with all attributes
    When I save the members using the MemberRepository
      | subject                                 | collectionName      | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1" can be retrieved from the database
    And The retrieved member has the same properties as the 1 member in the table

  Scenario: The repository can delete all members of a certain collection
    When I save the members using the MemberRepository
      | subject                                 | collectionName      | sequenceNr | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:23:05 |
      | http://test-data/gipod/1/1              | gipod               | [blank]    | http://test-data/gipod/1              | 2022-08-12T18:00:00 |
      | http://test-data/mobility-hindrance/1/3 | mobility-hindrances | [blank]    | http://test-data/mobility-hindrance/1 | 2022-08-12T18:48:35 |
      | http://test-data/gipod/1/2              | gipod               | [blank]    | http://test-data/gipod/1              | 2022-08-12T18:48:00 |
    Then The member with collection "gipod" and subject "http://test-data/gipod/1/1" will exist
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1" will exist
    When I delete collection "gipod"
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1" will exist
    And The member with collection "gipod" and subject "http://test-data/gipod/1/1" will not exist
    And The member with collection "gipod" and subject "http://test-data/gipod/1/2" will not exist

  Scenario: The repository can indicate if members exist or not
    When I save the members using the MemberRepository
      | subject                                 | collectionName      | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1" will exist
    And The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/fantasy-id" will not exist

  Scenario: Delete a member with a certain id
    When I save the members using the MemberRepository
      | subject                                 | collectionName      | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2022-08-12T18:35:00 |
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1" will exist
    When I delete the member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1"
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/2" will exist
    And The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1" will not exist

  Scenario: Members with the same id are not bulk inserted in the MemberRepository
    When I save the members using the MemberRepository
      | subject                                 | collectionName      | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/2 | 2022-08-12T18:35:00 |
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1" will not exist

  Scenario: Members with the same id are not inserted in the MemberRepository
    When I save the members using the MemberRepository
      | subject                                 | collectionName      | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/1 | 2022-08-12T18:18:00 |
    And I save the members using the MemberRepository
      | subject                                 | collectionName      | versionOf                             | timestamp           |
      | http://test-data/mobility-hindrance/1/1 | mobility-hindrances | http://test-data/mobility-hindrance/2 | 2022-08-12T18:35:00 |
      | http://test-data/mobility-hindrance/1/2 | mobility-hindrances | http://test-data/mobility-hindrance/3 | 2022-08-12T18:35:00 |
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/1" will exist
    Then The member with collection "mobility-hindrances" and subject "http://test-data/mobility-hindrance/1/2" will not exist
