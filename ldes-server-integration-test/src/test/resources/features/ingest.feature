Feature: LDES Server basic Ingest functionality

  Scenario: The LDES server supports different mime types for ingestion
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest the data described in "data/input/members/member_turtle.ttl" the collection "mobility-hindrances"
    And I ingest the data described in "data/input/members/member_nquads.nq" the collection "mobility-hindrances"
    And I ingest the data described in "data/input/members/member_ntriples.nt" the collection "mobility-hindrances"
    And I ingest the data described in "data/input/members/member_jsonld.jsonld" the collection "mobility-hindrances"
    Then I can fetch the TreeNode "/mobility-hindrances/paged?pageNumber=1" and it contains 4 members and the expected response is equal to "data/output/treenode_different_ingest_content_type_pageNumber_1.ttl"
    And I delete the eventstream "mobility-hindrances"

  Scenario: A small LDES can be ingested into the LDES server and a treenode can be fetched
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest 1016 members to the collection "mobility-hindrances"
    Then I can fetch the TreeNode "/mobility-hindrances/paged?pageNumber=1" and it contains 1016 members and the expected response is equal to "data/output/treenode_small_ldes_pageNumber_1.ttl"
    And I delete the eventstream "mobility-hindrances"

  Scenario: Server Supports Multi LDES
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    And I create the eventstream "data/input/eventstreams/activity.ttl"
    When I ingest 2 members of type "data/input/members/mob-hind.template.ttl" to the collection "mobility-hindrances"
    And I ingest 5 members of type "data/input/members/activity.template.ttl" to the collection "activities"
    Then the LDES "mobility-hindrances" contains 2 members
    And the LDES "activities" contains 5 members
    When I fetch the root "paged" fragment of "mobility-hindrances"
    And I fetch the next fragment through the first "Relation"
    Then this fragment contains 2 members
    When I fetch the root "paged" fragment of "activities"
    And I fetch the next fragment through the first "Relation"
    Then this fragment contains 5 members