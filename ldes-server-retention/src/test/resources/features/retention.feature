Feature: Execute RetentionService

  Background:
    Given an EventStream with the following properties
      | collection          |  | timestampPath                             | versionOfPath                        |
      | mobility-hindrances |  | http://www.w3.org/ns/prov#generatedAtTime | http://purl.org/dc/terms/isVersionOf |
    And the following Members are ingested
      | id                                       | collectionName      | versionOf                              | timestamp               | sequenceNumber |
      | http://test-data/mobility-hindrances/1/1 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2013-07-01T00:00:00.000 | 1              |
      | http://test-data/mobility-hindrances/1/2 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-02T00:00:00.000 | 2              |
      | http://test-data/mobility-hindrances/1/3 | mobility-hindrances | http://test-data/mobility-hindrances/1 | 2023-07-02T00:00:00.000 | 2              |
      | http://test-data/mobility-hindrances/2/1 | mobility-hindrances | http://test-data/mobility-hindrances/2 | 2013-07-01T00:00:00.000 | 3              |
      | http://test-data/mobility-hindrances/2/2 | mobility-hindrances | http://test-data/mobility-hindrances/2 | 2013-07-02T00:00:00.000 | 4              |
      | http://test-data/mobility-hindrances/2/3 | mobility-hindrances | http://test-data/mobility-hindrances/2 | 2023-07-03T00:00:00.000 | 5              |
      | http://test-data/mobility-hindrances/2/4 | mobility-hindrances | http://test-data/mobility-hindrances/2 | 2023-07-04T00:00:00.000 | 6              |
      | http://test-data/mobility-hindrances/3/1 | mobility-hindrances | http://test-data/mobility-hindrances/3 | 2013-07-01T00:00:00.000 | 7              |
      | http://test-data/mobility-hindrances/3/2 | mobility-hindrances | http://test-data/mobility-hindrances/3 | 2013-07-02T00:00:00.000 | 8              |
      | http://test-data/mobility-hindrances/3/3 | mobility-hindrances | http://test-data/mobility-hindrances/3 | 2013-07-03T00:00:00.000 | 9              |
      | http://test-data/mobility-hindrances/3/4 | mobility-hindrances | http://test-data/mobility-hindrances/3 | 2013-07-04T00:00:00.000 | 10             |
      | http://test-data/mobility-hindrances/3/5 | mobility-hindrances | http://test-data/mobility-hindrances/3 | 2023-07-05T00:00:00.000 | 11             |

  Scenario: TIME-BASED RETENTION
    When a view with the following properties is created
      | viewName                       | rdfDescriptionFileName                           |
      | mobility-hindrances/time-based | retentionpolicy/timebased/valid_timebased_it.ttl |
    And the following members are allocated to the view "mobility-hindrances/time-based"
      | http://test-data/mobility-hindrances/1/1 |
      | http://test-data/mobility-hindrances/1/2 |
      | http://test-data/mobility-hindrances/1/3 |
      | http://test-data/mobility-hindrances/2/1 |
      | http://test-data/mobility-hindrances/2/2 |
      | http://test-data/mobility-hindrances/2/3 |
      | http://test-data/mobility-hindrances/2/4 |
      | http://test-data/mobility-hindrances/3/1 |
      | http://test-data/mobility-hindrances/3/2 |
      | http://test-data/mobility-hindrances/3/3 |
      | http://test-data/mobility-hindrances/3/4 |
      | http://test-data/mobility-hindrances/3/5 |
    And wait for 3 seconds until the scheduler has executed at least once
    Then the view "mobility-hindrances/time-based" only contains following members
      | http://test-data/mobility-hindrances/1/2 |
      | http://test-data/mobility-hindrances/1/3 |
      | http://test-data/mobility-hindrances/2/3 |
      | http://test-data/mobility-hindrances/2/4 |
      | http://test-data/mobility-hindrances/3/5 |

  Scenario: VERSION-BASED RETENTION
    When a view with the following properties is created
      | viewName                          | rdfDescriptionFileName                                 |
      | mobility-hindrances/version-based | retentionpolicy/versionbased/valid_versionbased_it.ttl |
    And the following members are allocated to the view "mobility-hindrances/version-based"
      | http://test-data/mobility-hindrances/1/1 |
      | http://test-data/mobility-hindrances/1/2 |
      | http://test-data/mobility-hindrances/1/3 |
      | http://test-data/mobility-hindrances/2/1 |
      | http://test-data/mobility-hindrances/2/2 |
      | http://test-data/mobility-hindrances/2/3 |
      | http://test-data/mobility-hindrances/2/4 |
      | http://test-data/mobility-hindrances/3/1 |
      | http://test-data/mobility-hindrances/3/2 |
      | http://test-data/mobility-hindrances/3/3 |
      | http://test-data/mobility-hindrances/3/4 |
      | http://test-data/mobility-hindrances/3/5 |
    And wait for 3 seconds until the scheduler has executed at least once
    Then the view "mobility-hindrances/version-based" only contains following members
      | http://test-data/mobility-hindrances/1/2 |
      | http://test-data/mobility-hindrances/1/3 |
      | http://test-data/mobility-hindrances/2/3 |
      | http://test-data/mobility-hindrances/2/4 |
      | http://test-data/mobility-hindrances/3/4 |
      | http://test-data/mobility-hindrances/3/5 |

  Scenario: TIME- AND VERSION_BASED RETENTION
    When a view with the following properties is created
      | viewName                                  | rdfDescriptionFileName                                               |
      | mobility-hindrances/time-and-versionbased | retentionpolicy/timeandversionbased/valid_timeandversionbased_it.ttl |
    And the following members are allocated to the view "mobility-hindrances/time-and-versionbased"
      | http://test-data/mobility-hindrances/1/1 |
      | http://test-data/mobility-hindrances/1/2 |
      | http://test-data/mobility-hindrances/1/3 |
      | http://test-data/mobility-hindrances/2/1 |
      | http://test-data/mobility-hindrances/2/2 |
      | http://test-data/mobility-hindrances/2/3 |
      | http://test-data/mobility-hindrances/2/4 |
      | http://test-data/mobility-hindrances/3/1 |
      | http://test-data/mobility-hindrances/3/2 |
      | http://test-data/mobility-hindrances/3/3 |
      | http://test-data/mobility-hindrances/3/4 |
      | http://test-data/mobility-hindrances/3/5 |
    And wait for 3 seconds until the scheduler has executed at least once
    Then the view "mobility-hindrances/time-and-versionbased" only contains following members
      | http://test-data/mobility-hindrances/1/2 |
      | http://test-data/mobility-hindrances/1/3 |
      | http://test-data/mobility-hindrances/2/3 |
      | http://test-data/mobility-hindrances/2/4 |
      | http://test-data/mobility-hindrances/3/5 |