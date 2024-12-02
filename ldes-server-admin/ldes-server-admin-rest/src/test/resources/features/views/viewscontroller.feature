Feature: AdminViewsRestController

  Scenario Outline: Add a view with retention policies
    Given an LDES server with an event stream
    When I POST a view from file "<fileName>" to "/admin/api/v1/eventstreams/name1/views"
    Then I obtain an HTTP status <status>
    Examples:
      | fileName                                    | status |
      | view/view-without-retention.ttl             | 201    |
      | view/view.ttl                               | 201    |
      | view/view-with-duplicate-retention.ttl      | 400    |
      | view/view-with-two-diff-retention.ttl       | 201    |
      | view/view-with-empty-retention.ttl          | 400    |
      | view/view-with-wrong-type-retention.ttl     | 400    |
      | view/view-with-wrong-type-fragmentation.ttl | 400    |
      | view/view-without-fragmentation.ttl         | 400    |
      | view/view-with-empty-fragmentation.ttl      | 201    |
      | view/view-with-multiple-fragmentations.ttl  | 201    |

  Scenario: Add a view to a non-existing event stream
    Given an LDES server with an event stream
    When I POST a view from file "view/view.ttl" to "/admin/api/v1/eventstreams/an-unique-and-non-existing-event-stream/views"
    Then I obtain an HTTP status 404
    And I check if there were no interactions with the db

  Scenario Outline: Delete a view
    Given an LDES server with an event stream
    When I DELETE a view with "<viewName>"
    Then I obtain an HTTP status <httpStatus>
    And I verify there was <deletions> db deletion
    Examples:
      | viewName           | httpStatus | deletions |
      | name1/view1        | 200        | 1         |
      | non-existing/view1 | 404        | 0         |

  Scenario: Get all views
    Given an LDES server with an event stream
    And the event stream contains two views
    When I GET the views of the event stream
    Then I obtain an HTTP status 200
    And I check the response body is isomorphic with the two views
    And I check if all views from collection "name1" were retrieved from the db

  Scenario: Get one view
    Given an LDES server with an event stream
    And the event stream contains two views
    When I GET a view of the event stream wit id "name1/view1"
    Then I obtain an HTTP status 200
    And I check the response body is isomorphic with the single view
    And I check if view with id "name1/view1" was retrieved from the db

  Scenario: Get a non-existing view
    Given an LDES server with an event stream
    And the event stream contains two views
    When I GET a view of the event stream wit id "name1/view3"
    Then I obtain an HTTP status 404
    And I check if view with id "name1/view3" was retrieved from the db

  Scenario: Get a view from a non-existing event stream
    When I GET a view of the event stream wit id "name3/view1"
    Then I obtain an HTTP status 404
    And I check if view with id "name3/view1" was retrieved from the db
