Feature: The LDES server supports different mime types for ingestion

  Scenario:
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest the member described in "data/input/members/member_turtle.ttl" the collection "mobility-hindrances"
    And I ingest the member described in "data/input/members/member_nquads.nq" the collection "mobility-hindrances"
    And I ingest the member described in "data/input/members/member_ntriples.nt" the collection "mobility-hindrances"
    And I ingest the member described in "data/input/members/member_jsonld.jsonld" the collection "mobility-hindrances"
    Then I can fetch the TreeNode "/mobility-hindrances/paged?pageNumber=1" and it contains 4 members and the expected response is equal to "data/output/treenode_different_ingest_content_type_pageNumber_1.ttl"
    And I delete the eventstream "mobility-hindrances"

