Feature: MemberIngestionController
  As a publisher, I want to update the shacle shape of a collection

  # Test 1 & 2 could be outlines
  Scenario: Ingest valid member
    Given a restcontroller for collection "restaurant" and memberType "http://example.com/restaurant#MenuItem"
    When I ingest a new member conform to shape "fileName"
    Then Status 200 is returned
    And I verify the new member is added

  Scenario Outline: Ingest members
    Given a restcontroller for collection "restaurant" and memberType "http://example.com/restaurant#MenuItem"
    When I ingest a new member conform to shape <fileName>
    Then Status <status> is returned
    And I verify the new member is <verificationStatus>
    Examples:
      | fileName               | status | verificationStatus |
      | "example-data-old.ttl" | 200    | added              |
      | "example-data-new.ttl" | 400    | not added          |

  Scenario: Ingest valid member after shape was updated
    Given a restcontroller for collection "restaurant" and memberType "http://example.com/restaurant#MenuItem"
    When I put the new shape to the server
    When I ingest a new member conform to shape "fileName"
    Then Status 200 is returned
    And I verify the new member is added