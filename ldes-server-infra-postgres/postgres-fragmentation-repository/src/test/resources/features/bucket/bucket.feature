Feature: BucketisedMemberRepository
  As a user
  I want to interact with the BucketisedMemberRepository to
  create, retrieve and delete BucketisedMembers

  Scenario: Saving and retrieving BucketisedMembers
    Given The following bucketisedMembers
      | memberId  | viewName                              | fragmentId                                          | sequenceNr |
      | member1   | mobility-hindrances/by-time-and-page  | /mobility-hindrances/by-time-and-page?year=2023     | 1          |
      | member1   | mobility-hindrances/by-time-and-page2 | /mobility-hindrances/by-time-and-page?year=2024     | 1          |
      | member2   | mobility-hindrances/by-time-and-page  | /mobility-hindrances/by-time-and-page?year=2023     | 2          |
      | member3   | parcels/by-time-and-page              | /parcels/by-time-and-page?year=2023                 | 1          |
    When I save the bucketisedMembers using the BucketisedMemberRepository
    Then The BucketisedMemberRepository contains all the members

  Scenario: Deleting a collection
    Given The following bucketisedMembers
      | memberId  | viewName                              | fragmentId                                          | sequenceNr |
      | member1   | mobility-hindrances/by-time-and-page  | /mobility-hindrances/by-time-and-page?year=2023     | 1          |
      | member1   | mobility-hindrances/by-time-and-page2 | /mobility-hindrances/by-time-and-page?year=2024     | 1          |
      | member2   | parcels/by-time-and-page              | /parcels/by-time-and-page?year=2023                 | 1          |
    When I save the bucketisedMembers using the BucketisedMemberRepository
    And I delete the members of collection "mobility-hindrances"
    Then The BucketisedMemberRepository does not contain the members of collection "mobility-hindrances"

  Scenario: Deleting a view
    Given The following bucketisedMembers
      | memberId  | viewName                              | fragmentId                                          | sequenceNr |
      | member1   | mobility-hindrances/by-time-and-page  | /mobility-hindrances/by-time-and-page?year=2023     | 1          |
      | member1   | mobility-hindrances/by-time-and-page2 | /mobility-hindrances/by-time-and-page?year=2024     | 1          |
      | member2   | parcels/by-time-and-page              | /parcels/by-time-and-page?year=2023                 | 1          |
    When I save the bucketisedMembers using the BucketisedMemberRepository
    And I delete the members of view "mobility-hindrances/by-time-and-page"
    Then The BucketisedMemberRepository does not contain the members of view "mobility-hindrances/by-time-and-page"