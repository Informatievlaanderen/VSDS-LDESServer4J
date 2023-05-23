Feature: views can be configured at runtime
  Scenario: Add a view to a collection
    When A PUT request is made to "/admin/api/v1/eventstreams/collection/views" with body from file "view-1.ttl"
    Then The ViewSpecification with id "collection/view1" is saved in the ViewRepository
    And HTTP Status code 200 is received