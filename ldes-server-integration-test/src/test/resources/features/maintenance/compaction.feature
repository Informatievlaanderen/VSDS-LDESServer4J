Feature: LDES Server Compaction

  Scenario: Execution Compaction
    Given I create the eventstream "data/input/eventstreams/compaction/mobility-hindrances_paginated_5.ttl"
    And I ingest 6 members of different versions
    And I ingest 5 members of the same version
    And I ingest 5 members of the same version
    And I ingest 3 members of different versions
    Then I wait until all members are fragmented
    Then wait until no fragments can be compacted
    And verify there are 5 pages
    And verify the following pages have no relation pointing to them
      | 2 |
      | 3 |
    And verify 3 pages have a relation pointing to a compacted page
    And verify the following pages have no members
      | 2 |
      | 3 |
    And verify the following pages no longer exist
      | 2 |
      | 3 |
    And the background processes did not fail

  Scenario Outline: Compaction With Retention Policy
    Given I create the eventstream "data/input/eventstreams/compaction/small-paged-observations.ttl"
    And I ingest 1 members of version 0 of template <template> for collection <collection>
    And I ingest 3 members of version 1 of template <template> for collection <collection>
    And I ingest 3 members of version 2 of template <template> for collection <collection>
    And I ingest 3 members of version 3 of template <template> for collection <collection>
    And I ingest 3 members of version 4 of template <template> for collection <collection>
    Then I wait until all members are fragmented
    Then wait until no fragments can be compacted
    And verify the following pages have no members
        | 1 |
        | 2 |
        | 3 |
        | 4 |
        | 5 |
        | 6 |
    And verify 3 members are connected to a compacted page
    Examples:
      | template                                       | collection     |
      | "data/input/members/person-state.template.ttl" | "observations" |

  Scenario: Retention Compaction And Deletion works fine together
    Given I create the eventstream "data/input/eventstreams/compaction/observations.ttl"
    And I start seeding 5 members every 15 seconds
    When I wait until the first page does not exits anymore
    Then the root page points to a compacted page
    And I only have one open page
    Then I stop seeding members
    And I delete the eventstream "observations"
    And the background processes did not fail
