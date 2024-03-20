Feature: LDES Server Retention

  @time-based
  Scenario Outline: Server provides timebased retention
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest <numberOfMembers> members of template <template> to the collection <collection>
    Then the first fragment of the <view> view in collection <collection> contains <beforeRetentionCount> members
    # Since all added members' timestamp values equal to their ingestion date, they should be removed after 15 seconds
    Then the first fragment of the <view> view in collection <collection> contains <afterRetentionCount> members
    Examples:
      | eventStreamDescriptionFile                                            | template                                           | numberOfMembers | collection            | view    | beforeRetentionCount | afterRetentionCount |
#      | "data/input/eventstreams/retention/mobility-hindrances_timebased.ttl" | "data/input/members/mob-hind.template.ttl"         | 30              | "mobility-hindrances" | "paged" | 30                   | 0                   |
      | "data/input/eventstreams/retention/observations/timebased.ttl"        | "data/input/members/two-observations.template.ttl" | 30              | "observations"        | "paged" | 60                   | 0                   |

  @version-based
  Scenario: Server provides version retention
    Given I create the eventstream "data/input/eventstreams/retention/mobility-hindrances_versionbased.ttl"
    When I ingest 30 members to the collection "mobility-hindrances"
    # Since all added members belong to the same version, only 10 are kept as defined by the retention policy
    Then the first fragment of the "paged" view in collection "mobility-hindrances" contains 10 members

  @combined @version-based-and-time-based
  Scenario: Server combines multiple retention policies
    Given I create the eventstream "data/input/eventstreams/retention/mobility-hindrances_combined.ttl"
    When I ingest 30 members to the collection "mobility-hindrances"
    # With the version based and timebased retention combined, only 5 members will remain even if they are more than 5s ago
    Then the first fragment of the "paged" view in collection "mobility-hindrances" contains 5 members