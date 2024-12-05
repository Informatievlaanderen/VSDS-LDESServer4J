Feature: LDES Server basic Ingest functionality

  Scenario: The LDES server supports different mime types for ingestion
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest the data described in "data/input/members/member_turtle.ttl" the collection "mobility-hindrances"
    And I ingest the data described in "data/input/members/member_nquads.nq" the collection "mobility-hindrances"
    And I ingest the data described in "data/input/members/member_ntriples.nt" the collection "mobility-hindrances"
    And I ingest the data described in "data/input/members/member_jsonld.jsonld" the collection "mobility-hindrances"
    Then I can fetch the TreeNode "/mobility-hindrances/paged?pageNumber=1" and it contains 4 members
    And The expected response is equal to "data/output/treenode_different_ingest_content_type_pageNumber_1.ttl"
    And I delete the eventstream "mobility-hindrances"

  Scenario: The LDES returns the correct http status code
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest the data described in "data/input/members/member_turtle.ttl" the collection "mobility-hindrances"
    Then The returned status code is 201
    When I ingest the data described in "data/input/members/member_turtle.ttl" the collection "mobility-hindrances"
    Then The returned status code is 200
    And I delete the eventstream "mobility-hindrances"

  Scenario: A small LDES can be ingested into the LDES server and a treenode can be fetched
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest 1016 members to the collection "mobility-hindrances"
    And I wait until all members are fragmented
    Then I can fetch the TreeNode "/mobility-hindrances/paged?pageNumber=1" and it contains 1016 members
    And The expected response is equal to "data/output/treenode_small_ldes_pageNumber_1.ttl"
    And I delete the eventstream "mobility-hindrances"

  Scenario: Server Supports Multi LDES
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    And I create the eventstream "data/input/eventstreams/activity.ttl"
    When I ingest 2 members of template "data/input/members/mob-hind.template.ttl" to the collection "mobility-hindrances"
    And I ingest 5 members of template "data/input/members/activity.template.ttl" to the collection "activities"
    Then the LDES "mobility-hindrances" contains 2 members
    And the LDES "activities" contains 5 members
    And I wait until all members are fragmented
    When I fetch the root "paged" fragment of "mobility-hindrances"
    And I fetch the next fragment through the first "Relation"
    Then this fragment contains 2 members
    When I fetch the root "paged" fragment of "activities"
    And I fetch the next fragment through the first "Relation"
    Then this fragment contains 5 members

  Scenario: Server supports multiple member types
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest 2 members of template "data/input/members/mob-hind.template.ttl" to the collection "mobility-hindrances"
    And I ingest 5 members of template "data/input/members/activity.template.ttl" to the collection "mobility-hindrances"
    Then the LDES "mobility-hindrances" contains 7 members
    And I wait until all members are fragmented
    When I fetch the root "paged" fragment of "mobility-hindrances"
    And I fetch the next fragment through the first "Relation"
    Then this fragment contains 7 members

  Scenario: Server can ingest state objects
    Given I create the eventstream "data/input/eventstreams/simpsons.ttl"
    When I ingest 2 files of state objects from folder "data/input/members/simpsons" to the collection "simpsons"
    Then the LDES "simpsons" contains 7 members
    And I wait until all members are fragmented
    When I fetch the root "paged" fragment of "simpsons"
    And I fetch the next fragment through the first "Relation"
    Then this fragment contains 7 members

  Scenario Outline: Server can skolemize on ingestion
    Given I create the eventstream <eventStreamFile>
    When I ingest 1 members of template "<memberTemplateFile>" to the collection "mobility-hindrances"
    Then the LDES "mobility-hindrances" contains <expectedMemberCount> members
    And I wait until all members are fragmented
    When I fetch the root "paged" fragment of "mobility-hindrances"
    And I fetch the next fragment through the first "Relation"
    Then this fragment contains <expectedMemberCount> members with 2 skolemized identifiers
    Examples:
      | eventStreamFile                                                         | memberTemplateFile                             | expectedMemberCount |
      | "data/input/eventstreams/skolemization/mobility-hindrances.version.ttl" | data/input/members/mob-hind.bnodes/version.ttl | 1                   |
      | "data/input/eventstreams/skolemization/mobility-hindrances.state.ttl"   | data/input/members/mob-hind.bnodes/state.ttl   | 2                   |
