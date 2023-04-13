Feature: MemberIngestionController
  As a publisher, I want to update the shacle shape of a collection

  Scenario: Validate against the new shacle shape
    Given a LdesMemberIngestionController for collection "restaurant" and memberType "http://example.com/restaurant#MenuItem"
    Then I create a MemberIngestionController
    When I ingest a new member with the old shape
    Then Status 200 is returned
    And I verify the new member is added
    When I ingest a new member with the new shape
    Then Status 400 is returned
    And I verify the new member is not added
    When I put the new shape to the server
    Then Status 200 is returned
    When I ingest a new member with the new shape
    Then Status 200 is returned
    And I verify the new member is added