Feature: Execute CompactionService

  Background:
    Given I create the eventstream "data/input/eventstreams/compaction/mobility-hindrances_paginated_5.ttl"
    And I ingest 6 members of different versions
    And I ingest 5 members of the same version
    And I ingest 5 members of the same version
    And I ingest 3 members of different versions

  Scenario: Execution Compaction
    Then wait for 11 seconds until compaction has executed at least once
    And verify there are 5 pages
    And verify the following pages have no relation pointing to them
      | 2 |
      | 3 |
    And verify the pages have a relation pointing to the new page 5
    And verify the following pages have no members
      | 2 |
      | 3 |
    And wait for 11 seconds until deletion has executed at least once
    And verify the following pages no longer exist
      | 2 |
      | 3 |