Feature: A small LDES can be ingested into the LDES server and a treenode can be fetched

  Scenario:
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest 1016 members to the collection "mobility-hindrances"
    Then I can fetch the TreeNode "/mobility-hindrances/paged?pageNumber=1" and it contains 1016 members and the expected response is equal to "data/output/treenode_small_ldes_pageNumber_1.ttl"
    And I delete the eventstream "mobility-hindrances"
