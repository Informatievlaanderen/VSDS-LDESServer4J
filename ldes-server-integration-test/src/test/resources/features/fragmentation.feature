Feature: LDES Server Fragmentation

  @pagination
  Scenario: Server Can Paginate an LDES
    Given I create the eventstream "data/input/eventstreams/fragmentation/mobility-hindrances.paged.ttl"
    When I ingest 617 members to the collection "mobility-hindrances"
    Then the LDES "mobility-hindrances" contains 617 members
    When I fetch the root "paged" fragment of "mobility-hindrances"
    And I fetch the next fragment through the first "Relation"
    Then this fragment only has 1 "Relation" relation
    And this fragment is immutable
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has 1 "Relation" relation
    And this fragment is immutable
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 17 members
    And this fragment is mutable
    And this fragment has no relations

  @geospatial
  Scenario: Server Can Geospatially Fragment an LDES
    Given I create the eventstream "data/input/eventstreams/fragmentation/mobility-hindrances.by-loc.ttl"
    And I ingest 6 members to the collection "mobility-hindrances"
    And the LDES "mobility-hindrances" contains 6 members
    When I fetch the root "by-loc" fragment of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has 3 "GeospatiallyContainsRelation" relation
    And this fragment is mutable
    When I fetch the geo-spatial fragment for tile '9/262/171' from the "by-loc" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 6 members
    When I fetch the geo-spatial fragment for tile '9/262/170' from the "by-loc" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 6 members
    When I fetch the geo-spatial fragment for tile '9/261/171' from the "by-loc" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 6 members

  @multi-view
  Scenario: Server Allows Multiple Views in an LDES
    Given I create the eventstream "data/input/eventstreams/fragmentation/mobility-hindrances.by-loc.ttl"
    And I ingest 6 members to the collection "mobility-hindrances"
    When I fetch the root "by-loc" fragment of "mobility-hindrances"
    And I fetch the next fragment through the first "Relation"
    Then this fragment only has 3 "GeospatiallyContainsRelation" relation
    When I fetch the root "by-loc" fragment of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation