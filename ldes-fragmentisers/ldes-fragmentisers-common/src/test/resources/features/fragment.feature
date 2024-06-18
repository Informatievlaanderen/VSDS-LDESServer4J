Feature: Fragmentation

  Scenario: Execution FragmentDeletion
    Given the following Fragments are present
      | fragmentIdentifier                        | immutable | nrOfMembersAdded | relation                                               | daysUntilDeletion |
      | /mobility-hindrances/by-page              | false     | 0                | /mobility-hindrances/by-page?pageNumber=1              | 5                 |
      | /mobility-hindrances/by-page?pageNumber=1 | true      | 10               | /mobility-hindrances/by-page?pageNumber=2              | 2                 |
      | /mobility-hindrances/by-page?pageNumber=2 | true      | 10               | /mobility-hindrances/by-page?pageNumber=3,/dummy/dummy | -2                |
      | /mobility-hindrances/by-page?pageNumber=3 | true      | 10               | /mobility-hindrances/by-page?pageNumber=4              | 0                 |
      | /mobility-hindrances/by-page?pageNumber=4 | true      | 10               | /mobility-hindrances/by-page?pageNumber=5,/dummy/dummy | 3                 |
      | /mobility-hindrances/by-page?pageNumber=5 | true      | 10               | /mobility-hindrances/by-page?pageNumber=6              | -4                |
      | /mobility-hindrances/by-page?pageNumber=6 | false     | 7                | [blank]                                                | 1                 |
    Then wait for 5 seconds until fragment deletion has executed at least once
    And verify the deletion of the following fragments
      | /mobility-hindrances/by-page?pageNumber=2 |
      | /mobility-hindrances/by-page?pageNumber=5 |
