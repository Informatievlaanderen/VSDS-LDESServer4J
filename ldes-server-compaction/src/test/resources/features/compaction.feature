Feature: Execute CompactionService

  Background:
    Given a view with the following properties
      | viewName                    | pageSize |
      | mobility-hindrances/by-page | 10       |
    And the following Fragments are available
      | url                                       | immutable | bucket   | view                         |
      | /mobility-hindrances/by-page              | false     | by-page  | mobility-hindrances/by-page |
      | /mobility-hindrances/by-page?pageNumber=1 | true      | by-page  | mobility-hindrances/by-page |
      | /mobility-hindrances/by-page?pageNumber=2 | true      | by-page  | mobility-hindrances/by-page |
      | /mobility-hindrances/by-page?pageNumber=3 | true      | by-page  | mobility-hindrances/by-page |
      | /mobility-hindrances/by-page?pageNumber=4 | true      | by-page  | mobility-hindrances/by-page |
      | /mobility-hindrances/by-page?pageNumber=5 | true      | by-page  | mobility-hindrances/by-page |
      | /mobility-hindrances/by-page?pageNumber=6 | false     | by-page  | mobility-hindrances/by-page |
    And the following members are present
      | pageId | collection          | bucketDescriptor | amount |
      | 2L     | mobility-hindrances |                  | 10     |
      | 3L     | mobility-hindrances |                  | 3      |
      | 4L     | mobility-hindrances |                  | 9      |
      | 5L     | mobility-hindrances |                  | 3      |
      | 6L     | mobility-hindrances |                  | 4      |
      | 7L     | mobility-hindrances |                  | 2      |

  Scenario: Execution Compaction
    Then wait for 5 seconds until compaction has executed at least once
    And verify there are 9 pages
    And verify update of predecessor relations
      | /mobility-hindrances/by-page                  |
    And verify fragmentation of members
      | fragmentId                                    | memberIds                                                                        |
      | /mobility-hindrances/by-page?pageNumber=1/2/3 | member1,member2,member3,member4,member5,member6,member7,member8,member9,member10 |
      | /mobility-hindrances/by-page?pageNumber=4/5   | member11,member12,member13,member14,member15,member16                            |