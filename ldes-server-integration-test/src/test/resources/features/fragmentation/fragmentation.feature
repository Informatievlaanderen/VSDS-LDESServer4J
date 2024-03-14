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
    When I fetch the "tile" fragment for '9/262/171' from the "by-loc" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 6 members
    When I fetch the "tile" fragment for '9/262/170' from the "by-loc" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 6 members
    When I fetch the "tile" fragment for '9/261/171' from the "by-loc" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 6 members

  @timebased
  Scenario: Server can do Timebased Fragmentation of an LDES
    Given I create the eventstream "data/input/eventstreams/fragmentation/mobility-hindrances.by-time.ttl"
    And I ingest 5 members to the collection "mobility-hindrances" with current timestamp
    And the LDES "mobility-hindrances" contains 5 members
    When I fetch the timebased fragment "by-time" fragment of this month of "mobility-hindrances"
    And I fetch the next fragment through the first "InBetweenRelation"
    And I fetch the next fragment through the first "Relation"

  @reference
  Scenario: Server Can Fragment an LDES using the Reference Fragmentation strategy
    Given I create the eventstream "data/input/eventstreams/fragmentation/mobility-hindrances.by-ref.ttl"
    And I ingest 6 members to the collection "mobility-hindrances"
    And the LDES "mobility-hindrances" contains 6 members
    When I fetch the root "by-ref" fragment of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has 1 "EqualToRelation" relation
    And this fragment is mutable
    When I fetch the "reference" fragment for 'http%3A%2F%2Ftest-data%2Fmobility-hindrance%2F1' from the "by-ref" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 6 members

  @nested-reference
  Scenario: Server Can Fragment an LDES using nested Reference Fragmentation strategies
    Given I create the eventstream "data/input/eventstreams/fragmentation/mobility-hindrances.by-nested-ref.ttl"
    When I ingest 2 members of template "data/input/members/mob-hind.template.ttl" to the collection "mobility-hindrances"
    And I ingest 5 members of template "data/input/members/activity.template.ttl" to the collection "mobility-hindrances"
    Then the LDES "mobility-hindrances" contains 7 members
    When I fetch the root "by-nested-ref" fragment of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has 2 "EqualToRelation" relation
    And this fragment is mutable
    When I fetch the "type" fragment for 'https%3A%2F%2Fdata.vlaanderen.be%2Fns%2Fmobiliteit%23Mobiliteitshinder' from the "by-nested-ref" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has 1 "EqualToRelation" relation
    And this fragment is mutable
    When I fetch the "/mobility-hindrances/by-nested-ref?type=https%3A%2F%2Fdata.vlaanderen.be%2Fns%2Fmobiliteit%23Mobiliteitshinder&version=http%3A%2F%2Ftest-data%2Fmobility-hindrance%2F1" fragment
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 2 members
    When I fetch the "type" fragment for 'https%3A%2F%2Fwww.w3.org%2Fns%2Factivitystreams%23Create' from the "by-nested-ref" view of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has 1 "EqualToRelation" relation
    And this fragment is mutable
    When I fetch the "/mobility-hindrances/by-nested-ref?type=https%3A%2F%2Fwww.w3.org%2Fns%2Factivitystreams%23Create&version=https%3A%2F%2Fwww.w3.org%2Fns%2Factivitystreams%231" fragment
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 5 members

  @multi-view
  Scenario: Server Allows Multiple Views in an LDES
    Given I create the eventstream "data/input/eventstreams/fragmentation/mobility-hindrances.by-loc.ttl"
    And I ingest 6 members to the collection "mobility-hindrances"
    When I fetch the root "by-loc" fragment of "mobility-hindrances"
    And I fetch the next fragment through the first "Relation"
    Then this fragment only has 3 "GeospatiallyContainsRelation" relation
    When I fetch the root "by-loc" fragment of "mobility-hindrances"
    Then this fragment only has 1 "Relation" relation