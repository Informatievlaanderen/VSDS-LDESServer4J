Feature: LdesFragmentRepository
  As a user
  I want to interact with the LdesFragmentRepository to save and retrieve LdesFragments

  Scenario: Saving and retrieving an LdesFragment
    Given The following ldesFragment
      | viewName                             | fragmentPairs         | immutable | softdeleted | numberOfMembers |
      | mobility-hindrance/by-name-and-page | substring,gent,page,1 | false     | false       | 23              |
    When I save the ldesFragment using the LdesFragmentRepository
    Then The ldesFragment with id "/mobility-hindrance/by-name-and-page?substring=gent&page=1" can be retrieved from the database
    And The retrieved ldesFragment has the same properties the orignal ldesFragment

