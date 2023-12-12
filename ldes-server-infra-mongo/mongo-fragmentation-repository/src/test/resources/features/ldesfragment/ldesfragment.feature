Feature: LdesFragmentRepository
  As a user
  I want to interact with the LdesFragmentRepository to save, retrieve and delete LdesFragments

  Scenario: Saving, retrieving and deleting an LdesFragment
    Given The following ldesFragments
      | viewName                             | fragmentPairs    | immutable | softdeleted | nrOfMembersAdded |
      | mobility-hindrances/by-time-and-page | [blank]          | false     | false       | 23               |
      | mobility-hindrances/by-time-and-page | year,2023,page,1 | false     | false       | 23               |
      | mobility-hindrances/by-time-and-page | year,2023,page,2 | false     | false       | 23               |
    When I save the ldesFragments using the LdesFragmentRepository
    Then the repository contains 3 ldesFragments with viewname "mobility-hindrances/by-time-and-page"
    And The ldesFragment with id "/mobility-hindrances/by-time-and-page" can be retrieved from the database
    And The retrieved ldesFragment has the same properties as ldesFragment 1
    And The ldesFragment with id "/mobility-hindrances/by-time-and-page?year=2023&page=1" can be retrieved from the database
    And The retrieved ldesFragment has the same properties as ldesFragment 2
    And The ldesFragment with id "/mobility-hindrances/by-time-and-page?year=2023&page=2" can be retrieved from the database
    And The retrieved ldesFragment has the same properties as ldesFragment 3
    When I delete the ldesFragments of viewName "mobility-hindrances/by-time-and-page"
    Then the repository contains 0 ldesFragments with viewname "mobility-hindrances/by-time-and-page"

  Scenario: Deleting a collection
    Given The following ldesFragments
      | viewName                             | fragmentPairs    | immutable | softdeleted | nrOfMembersAdded |
      | mobility-hindrances/by-time-and-page | [blank]          | false     | false       | 23               |
      | mobility-hindrances/by-time-and-page | year,2023,page,1 | false     | false       | 23               |
      | mobility-hindrances/by-time-and-page | year,2023,page,2 | false     | false       | 23               |
      | parcels/by-page                      | page,1           | false     | false       | 23               |
      | parcels/by-page                      | page,2           | false     | false       | 21               |
    And I save the ldesFragments using the LdesFragmentRepository
    Then the repository contains 3 ldesFragments with viewname "mobility-hindrances/by-time-and-page"
    And the repository contains 2 ldesFragments with viewname "parcels/by-page"
    When I delete the collection "mobility-hindrances"
    Then the repository contains 0 ldesFragments with viewname "mobility-hindrances/by-time-and-page"
    And the repository contains 2 ldesFragments with viewname "parcels/by-page"
    When I delete the collection "parcels"
    Then the repository contains 0 ldesFragments with viewname "mobility-hindrances/by-time-and-page"
    Then the repository contains 0 ldesFragments with viewname "parcels/by-page"

  Scenario: Retrieve open child fragment
    Given The following ldesFragments
      | viewName                    | fragmentPairs | immutable | softdeleted | nrOfMembersAdded |
      | mobility-hindrances/by-page | [blank]       | false     | false       | 23               |
      | mobility-hindrances/by-page | page,1        | true      | false       | 23               |
      | mobility-hindrances/by-page | page,2        | false     | false       | 23               |
    And I save the ldesFragments using the LdesFragmentRepository
    Then the repository contains 3 ldesFragments with viewname "mobility-hindrances/by-page"
    When I retrieve the open child fragment of fragment 1
    Then The retrieved ldesFragment has the same properties as ldesFragment 3

  Scenario: Retrieve root fragment
    Given The following ldesFragments
      | viewName                    | fragmentPairs | immutable | softdeleted | nrOfMembersAdded |
      | mobility-hindrances/by-page | [blank]       | false     | false       | 23               |
      | mobility-hindrances/by-page | page,1        | true      | false       | 23               |
      | mobility-hindrances/by-page | page,2        | false     | false       | 23               |
    And I save the ldesFragments using the LdesFragmentRepository
    Then the repository contains 3 ldesFragments with viewname "mobility-hindrances/by-page"
    When I retrieve the root fragment of the view with viewname "mobility-hindrances/by-page"
    Then The retrieved ldesFragment has the same properties as ldesFragment 1

  Scenario: Increment number of members
    Given The following ldesFragments
      | viewName                    | fragmentPairs | immutable | softdeleted | nrOfMembersAdded |
      | mobility-hindrances/by-page | [blank]       | false     | false       | 23               |
      | mobility-hindrances/by-page | page,1        | true      | false       | 23               |
    And I save the ldesFragments using the LdesFragmentRepository
    Then the repository contains 2 ldesFragments with viewname "mobility-hindrances/by-page"
    When I increment the number of members of fragment 2
    And The ldesFragment with id "/mobility-hindrances/by-page?page=1" can be retrieved from the database
    Then The retrieved ldesFragment has 24 members

  Scenario: Retrieve Fragment By Outgoing Relation
    Given The following ldesFragments with relations
      | viewName                    | fragmentPairs | relations                                                                 |
      | mobility-hindrances/by-page | [blank]       | /mobility-hindrances/by-page?page=1,/mobility-hindrances/by-page?page=1/2 |
      | mobility-hindrances/by-page | page,1        | /mobility-hindrances/by-page?page=2                                       |
      | mobility-hindrances/by-page | page,2        | /mobility-hindrances/by-page?page=3                                       |
      | mobility-hindrances/by-page | page,1/2      | /mobility-hindrances/by-page?page=3                                       |
      | mobility-hindrances/by-page | page,3        | [blank]                                                                   |
    And I save the ldesFragments using the LdesFragmentRepository
    Then the repository the following fragments with outgoing relations
      | outgoingRelation                      | fragmentIds                                                               |
      | /mobility-hindrances/by-page          | [blank]                                                                   |
      | /mobility-hindrances/by-page?page=1   | /mobility-hindrances/by-page                                              |
      | /mobility-hindrances/by-page?page=2   | /mobility-hindrances/by-page?page=1                                       |
      | /mobility-hindrances/by-page?page=1/2 | /mobility-hindrances/by-page                                              |
      | /mobility-hindrances/by-page?page=3   | /mobility-hindrances/by-page?page=2,/mobility-hindrances/by-page?page=1/2 |

  Scenario: Delete Fragment
    Given The following ldesFragments with relations
      | viewName                    | fragmentPairs | relations                                                                 |
      | mobility-hindrances/by-page | [blank]       | /mobility-hindrances/by-page?page=1,/mobility-hindrances/by-page?page=1/2 |
      | mobility-hindrances/by-page | page,1        | /mobility-hindrances/by-page?page=2                                       |
      | mobility-hindrances/by-page | page,2        | /mobility-hindrances/by-page?page=3                                       |
      | mobility-hindrances/by-page | page,1/2      | /mobility-hindrances/by-page?page=3                                       |
      | mobility-hindrances/by-page | page,3        | [blank]                                                                   |
    And I save the ldesFragments using the LdesFragmentRepository
    When I delete the fragment "/mobility-hindrances/by-page?page=3"
    Then The repository has the following fragments left
      | viewName                    | fragmentPairs | relations                                                                 |
      | mobility-hindrances/by-page | [blank]       | /mobility-hindrances/by-page?page=1,/mobility-hindrances/by-page?page=1/2 |
      | mobility-hindrances/by-page | page,1        | /mobility-hindrances/by-page?page=2                                       |
      | mobility-hindrances/by-page | page,2        | [blank]                                       |
      | mobility-hindrances/by-page | page,1/2      | [blank]                                       |
    When I delete the fragment "/mobility-hindrances/by-page?page=2"
    Then The repository has the following fragments left
      | viewName                    | fragmentPairs | relations                                                                 |
      | mobility-hindrances/by-page | [blank]       | /mobility-hindrances/by-page?page=1,/mobility-hindrances/by-page?page=1/2 |
      | mobility-hindrances/by-page | page,1        | [blank]                                       |
      | mobility-hindrances/by-page | page,1/2      | [blank]                                       |
    When I delete the fragment "/mobility-hindrances/by-page?page=1/2"
    Then The repository has the following fragments left
      | viewName                    | fragmentPairs | relations                                                                 |
      | mobility-hindrances/by-page | [blank]       | /mobility-hindrances/by-page?page=1 |
      | mobility-hindrances/by-page | page,1        | [blank]                                        |
    When I delete the fragment "/mobility-hindrances/by-page?page=1"
    Then The repository has the following fragments left
      | viewName                    | fragmentPairs | relations                                                                 |
      | mobility-hindrances/by-page | [blank]       |  [blank]  |
    When I delete the fragment "/mobility-hindrances/by-page"
    Then the repository contains 0 ldesFragments with viewname "mobility-hindrances/by-page"

