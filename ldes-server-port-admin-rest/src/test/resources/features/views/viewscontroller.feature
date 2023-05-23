Feature: views can be configured at runtime
  Scenario: get all views of a collection
    Given a dbs containing multiple eventstreams
    When the clients calls "/admin/api/v1/eventstreams"
    Then the clients receives HTTP status 200
    And the clients receives a valid list of event streams