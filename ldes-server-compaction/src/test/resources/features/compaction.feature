Feature: Execute CompactionService

  Background:
    Given a view with the following properties
      | viewName                    | pageSize |
      | mobility-hindrances/by-page | 10       |
    And the following Fragments are available
      | fragmentIdentifier                       | immutable | relation                                 |
      | mobility-hindrances/by-page              | false     | mobility-hindrances/by-page?pageNumber=1 |
      | mobility-hindrances/by-page?pageNumber=1 | true      | mobility-hindrances/by-page?pageNumber=2 |
      | mobility-hindrances/by-page?pageNumber=2 | true      | mobility-hindrances/by-page?pageNumber=3 |
      | mobility-hindrances/by-page?pageNumber=3 | true      | mobility-hindrances/by-page?pageNumber=4 |
      | mobility-hindrances/by-page?pageNumber=4 | true      | mobility-hindrances/by-page?pageNumber=5 |
      | mobility-hindrances/by-page?pageNumber=5 | true      | mobility-hindrances/by-page?pageNumber=6 |
      | mobility-hindrances/by-page?pageNumber=6 | false     | [blank]                                  |

  Scenario: Execution Compaction
    Then wait sometime