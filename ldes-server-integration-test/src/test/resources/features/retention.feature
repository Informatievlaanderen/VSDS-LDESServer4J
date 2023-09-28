Feature: LDES Server Retention

  @time-based
  Scenario: Server provides timebased retention
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_timebased.ttl"
    When I ingest 30 members to the collection "mobility-hindrances"
    Then the collection "mobility-hindrances" contains 30 members
    # Since all added members' timestamp values equal to their ingestion date, they should be removed after 15 seconds
    Then the collection "mobility-hindrances" contains 0 members
    And I delete the eventstream "mobility-hindrances"

  @version-based
  Scenario: Server provides version retention
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_versionbased.ttl"
    When I ingest 30 members to the collection "mobility-hindrances"
    # Since all added members belong to the same version, only 10 are kept as defined by the retention policy
    Then the collection "mobility-hindrances" contains 10 members
    And I delete the eventstream "mobility-hindrances"

  @point-in-time
  Scenario: Server provides point in time retention
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_pointintime.ttl"
    When I ingest 30 members to the collection "mobility-hindrances"
    # Since all added members have a timestamp after the set point in time, all members are retained
    Then the collection "mobility-hindrances" contains 30 members
    And I delete the eventstream "mobility-hindrances"

  @combined @version-based-and-point-in-time
  Scenario: Server combines multiple retention policies
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_combined.ttl"
    When I ingest 30 members to the collection "mobility-hindrances"
    # With the version based and timebased retention combined, only 5 members will remain even if they are more than 5s ago
    Then the collection "mobility-hindrances" contains 5 members
    And I delete the eventstream "mobility-hindrances"