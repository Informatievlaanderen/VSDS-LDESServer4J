Feature: MemberToFragmentRepository
  As a user
  I want to interact with the MemberToFragmentRepository to
  create, retrieve and delete members that need to be fragmented

  Scenario: Saving, retrieving and deleting a MemberToFragment
    Given I have the following collections
      | collectionName      | views                     |
      | mobility-hindrances | by-page, by-name-and-page |
      | parcels             | by-page                   |
    And I create the following members
      | collectionName      | sequenceNr |  | memberId                    |
      | mobility-hindrances | 1          |  | mobility-hindrances/urn:id1 |
      | mobility-hindrances | 2          |  | mobility-hindrances/urn:id2 |
      | parcels             | 1          |  | parcels/urn:id1             |
    When I request the next member for view "mobility-hindrances/by-page"