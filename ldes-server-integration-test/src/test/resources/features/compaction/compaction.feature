Feature: Execute CompactionService

  Background:
    Given I create the eventstream "data/input/eventstreams/compaction/mobility-hindrances_paginated_10.ttl"
    And the members are ingested

  Scenario: Execution Compaction
    Then wait for 60 seconds until compaction has executed at least once
    And verify there are 6 pages
    And verify update of predecessor relations
      | 2 |
      | 3 |
      | 4 |
    And verify fragmentation of members
      | 2 |
      | 3 |
      | 4 |