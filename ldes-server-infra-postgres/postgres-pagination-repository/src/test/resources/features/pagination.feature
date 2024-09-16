Feature: Pagination Interactions

  Scenario: Assign members to page (5/10 members)
    Given I have 5 unpaged members in one bucket
    When I assign the members to the page
    Then I expect no more unpaged members
    And the open page has space for 5 more members

  Scenario: Assign members to page (10/10 members)
    Given I have 10 unpaged members in one bucket
    When I assign the members to the page
    Then I expect a new page is created
    And I expect no more unpaged members

  Scenario: Create new page
    When I create a page given the mutable page
    Then I expect a new page is created
    And The old page has a generic relation to the new page


