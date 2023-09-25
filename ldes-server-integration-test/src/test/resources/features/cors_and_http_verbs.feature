Feature: The LDES server has access control headers and etag

  Scenario:
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest 1 members to the collection "mobility-hindrances"
    Then The response from requesting the url "/mobility-hindrances" has access control headers and an etag
    And The response from requesting the url "/mobility-hindrances/paged" has access control headers and an etag
    And The response from requesting the url "/mobility-hindrances/paged?pageNumber=1" has access control headers and an etag
    And I delete the eventstream "mobility-hindrances"
