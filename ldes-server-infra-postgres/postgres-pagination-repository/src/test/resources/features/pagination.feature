Feature: Pagination Interactions

  Scenario: Get open page

  Scenario: Assign members to page
    Given I have 5 unpaged members for bucket "x"
    And I have a page with capacity of 10
    When I assign the members to the page
    Then I expect no more unpaged members

  Scenario: Assign members to page (filled page)
    Given I have 10 unpaged members for bucket "x"
    And I have a page with capacity of 10
    When I assign the members to the page
    Then I expect a new page is created
    And I expect no more unpaged members

  Scenario: Create new page
    Given I have a mutable Page
    When I create a page given the mutable page
    Then I expect a new page is created
    And The old page has a generic relation to the new page


