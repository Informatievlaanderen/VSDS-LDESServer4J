Feature: LdesFragmentRepository
  As a user
  I want to interact with the LdesFragmentRepository to save, retrieve and delete LdesFragments

  Scenario: Saving, retrieving and deleting an LdesFragment
    Given The following ldesFragments
      | viewName                            | fragmentPairs         | immutable | softdeleted | numberOfMembers |
      | mobility-hindrance/by-name-and-page | [blank]               | false     | false       | 23              |
      | mobility-hindrance/by-name-and-page | substring,gent,page,1 | false     | false       | 23              |
      | mobility-hindrance/by-name-and-page | substring,gent,page,2 | false     | false       | 23              |
    When I save the ldesFragments using the LdesFragmentRepository
    Then the repository contains 3 ldesFragments with viewname "mobility-hindrance/by-name-and-page"
    And The ldesFragment with id "/mobility-hindrance/by-name-and-page" can be retrieved from the database
    And The retrieved ldesFragment has the same properties as ldesFragment 1
    And The ldesFragment with id "/mobility-hindrance/by-name-and-page?substring=gent&page=1" can be retrieved from the database
    And The retrieved ldesFragment has the same properties as ldesFragment 2
    And The ldesFragment with id "/mobility-hindrance/by-name-and-page?substring=gent&page=2" can be retrieved from the database
    And The retrieved ldesFragment has the same properties as ldesFragment 3
    When I delete the ldesFragments of viewName "mobility-hindrance/by-name-and-page"
    Then the repository contains 0 ldesFragments with viewname "mobility-hindrance/by-name-and-page"

