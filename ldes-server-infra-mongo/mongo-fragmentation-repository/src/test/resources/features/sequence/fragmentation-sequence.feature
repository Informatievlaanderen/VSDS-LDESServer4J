Feature: FragmentSequenceRepository
  As a user
  I want to interact with the FragmentSequenceRepository to
  create, retrieve and delete fragmentation sequences

  Scenario: Saving and updating a FragmentSequence
    When I save a new FragmentSequence for view "mobility-hindrances/by-page" with sequenceNr 200
    And I request the last processed sequence for view "mobility-hindrances/by-page"
    Then I receive a FragmentSequence with sequenceNr 200
    When I save a new FragmentSequence for view "mobility-hindrances/by-page" with sequenceNr 300
    And I request the last processed sequence for view "mobility-hindrances/by-page"
    Then I receive a FragmentSequence with sequenceNr 300

  Scenario: Deleting a FragmentSequence
    When I save a new FragmentSequence for view "mobility-hindrances/by-page" with sequenceNr 200
    And I request the last processed sequence for view "mobility-hindrances/by-page"
    Then I receive a FragmentSequence with sequenceNr 200
    When I delete the sequence for view "mobility-hindrances/by-page"
    And I request the last processed sequence for view "mobility-hindrances/by-page"
    Then I do not find a FragmentSequence