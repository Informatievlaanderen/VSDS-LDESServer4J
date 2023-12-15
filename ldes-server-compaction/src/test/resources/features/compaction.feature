Feature: Execute CompactionService

  Background:
    Given a view with the following properties
      | viewName                    | pageSize |
      | mobility-hindrances/by-page | 10       |
#    Note that we add a dummy relation to fragment /mobility-hindrances/by-page?pageNumber=2 and /mobility-hindrances/by-page?pageNumber=4 to account for the relations added after creation of fragment /mobility-hindrances/by-page?pageNumber=1/2 and /mobility-hindrances/by-page?pageNumber=3/4
    And the following Fragments are available
      | fragmentIdentifier                        | immutable | nrOfMembersAdded | relation                                               |
      | /mobility-hindrances/by-page              | false     | 0                | /mobility-hindrances/by-page?pageNumber=1              |
      | /mobility-hindrances/by-page?pageNumber=1 | true      | 10               | /mobility-hindrances/by-page?pageNumber=2              |
      | /mobility-hindrances/by-page?pageNumber=2 | true      | 10               | /mobility-hindrances/by-page?pageNumber=3,/dummy/dummy |
      | /mobility-hindrances/by-page?pageNumber=3 | true      | 10               | /mobility-hindrances/by-page?pageNumber=4              |
      | /mobility-hindrances/by-page?pageNumber=4 | true      | 10               | /mobility-hindrances/by-page?pageNumber=5,/dummy/dummy |
      | /mobility-hindrances/by-page?pageNumber=5 | true      | 10               | /mobility-hindrances/by-page?pageNumber=6              |
      | /mobility-hindrances/by-page?pageNumber=6 | false     | 7                | [blank]                                                |
    And the following allocations are present
      | fragmentIdentifier                        | members                          |
      | /mobility-hindrances/by-page?pageNumber=1 | member1,member2,member3          |
      | /mobility-hindrances/by-page?pageNumber=2 | member4,member5,member6          |
      | /mobility-hindrances/by-page?pageNumber=3 | member7,member8,member9,member10 |
      | /mobility-hindrances/by-page?pageNumber=4 | member11,member12,member13       |
      | /mobility-hindrances/by-page?pageNumber=5 | member14,member15,member16       |
      | /mobility-hindrances/by-page?pageNumber=6 | member17                         |

  Scenario: Execution Compaction
    Then wait for 5 seconds until compaction has executed at least once
    And verify creation of the following fragments
      | /mobility-hindrances/by-page?pageNumber=1/2/3 |
      | /mobility-hindrances/by-page?pageNumber=4/5   |
    And verify update of predecessor relations
      | /mobility-hindrances/by-page                  |
    And verify fragmentation of members
      | fragmentId                                    | memberIds                                                                        |
      | /mobility-hindrances/by-page?pageNumber=1/2/3 | member1,member2,member3,member4,member5,member6,member7,member8,member9,member10 |
      | /mobility-hindrances/by-page?pageNumber=4/5   | member11,member12,member13,member14,member15,member16                            |