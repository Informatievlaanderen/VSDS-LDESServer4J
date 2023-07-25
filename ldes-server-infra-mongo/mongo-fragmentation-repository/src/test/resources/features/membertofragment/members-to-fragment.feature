Feature: MemberToFragmentRepository
  As a user
  I want to interact with the MemberToFragmentRepository to
  create, retrieve and delete members that need to be fragmented

  Scenario: Saving, retrieving and deleting a MemberToFragment
    Given I have the following collections
      | collectionName      | views                    |
      | mobility-hindrances | by-page,by-name-and-page |
      | parcels             | by-page                  |
    And I create the following members
      | collectionName      | sequenceNr | memberId                    |
      | mobility-hindrances | 2          | mobility-hindrances/urn:ida |
      | mobility-hindrances | 111        | mobility-hindrances/urn:idb |
      | mobility-hindrances | 3          | mobility-hindrances/urn:idc |
      | parcels             | 15         | parcels/urn:ida             |
    When I request the next member for view "mobility-hindrances/by-page"
    Then I find the member with id "mobility-hindrances/urn:ida" and sequenceNr 2
    When I delete the member with view "mobility-hindrances/by-page" and sequenceNr 2
    And I request the next member for view "mobility-hindrances/by-page"
    Then I find the member with id "mobility-hindrances/urn:idc" and sequenceNr 3
    When I delete the member with view "mobility-hindrances/by-page" and sequenceNr 3
    And I request the next member for view "mobility-hindrances/by-page"
    Then I find the member with id "mobility-hindrances/urn:idb" and sequenceNr 111
    When I delete the member with view "mobility-hindrances/by-page" and sequenceNr 111
    And I request the next member for view "mobility-hindrances/by-page"
    Then I do not find a member
    When I request the next member for view "mobility-hindrances/by-name-and-page"
    Then I find the member with id "mobility-hindrances/urn:ida" and sequenceNr 2
    When I request the next member for view "parcels/by-page"
    Then I find the member with id "parcels/urn:ida" and sequenceNr 15
    When I request the next member for view "not-existing/fantasy-view"
    Then I do not find a member
