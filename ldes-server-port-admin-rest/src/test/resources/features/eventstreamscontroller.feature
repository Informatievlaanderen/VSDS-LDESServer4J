Feature: event streams can be configured at runtime
  Scenario: retrieve all event streams
    Given a db containing multiple eventstreams
    When the client calls "/admin/api/v1/eventstreams"
    Then the client receives list of event streams