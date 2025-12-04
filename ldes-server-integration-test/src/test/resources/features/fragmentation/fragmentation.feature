Feature: LDES Server Fragmentation

#  @pagination
#  Scenario Outline: Server Can Paginate an LDES
#    Given I create the eventstream <eventStreamDescriptionFile>
#    When I ingest 617 members of template <template> to the collection <collection>
#    Then the LDES <collection> contains <ingestedMemberCount> members
#    And I wait until all members are fragmented
#    Then all members of <collection> are marked as fragmented
#    When I fetch the root "paged" fragment of <collection>
#    And I fetch the next fragment through the first "Relation"
#    Then this fragment only has 1 "Relation" relation
#    And this fragment is immutable
#    When I fetch the next fragment through the first "Relation"
#    Then this fragment only has 1 "Relation" relation
#    And this fragment is immutable
#    When I fetch the next fragment through the first "Relation"
#    And this fragment contains <restCount> members
#    And this fragment is mutable
#    And this fragment has no relations
#    Examples:
#      | eventStreamDescriptionFile                                            | template                                           | collection            | ingestedMemberCount | restCount |
#      | "data/input/eventstreams/fragmentation/mobility-hindrances.paged.ttl" | "data/input/members/mob-hind.template.ttl"         | "mobility-hindrances" | 617                 | 17        |
#      | "data/input/eventstreams/fragmentation/observations/paged.ttl"        | "data/input/members/two-observations.template.ttl" | "observations"        | 1234                | 34        |
#
#  @refragmentation
#  Scenario Outline: Server Can Refragment an LDES
#    Given I create the eventstream <eventStreamDescriptionFile>
#    When I ingest 617 members of template <template> to the collection <collection>
#    Then the LDES <collection> contains <ingestedMemberCount> members
#    And I wait until all members are fragmented
#    # The members are unfragmented, as there's no view at this time. The fragmentation will start after the view is added.
#    Then all members of <collection> are marked as unfragmented
#    And I create the view <viewDescriptionFile>
#    And I wait until all members are fragmented
#    Then all members of <collection> are marked as fragmented
#    When I fetch the root "paged" fragment of <collection>
#    And I fetch the next fragment through the first "Relation"
#    Then this fragment only has 1 "Relation" relation
#    And this fragment is immutable
#    When I fetch the next fragment through the first "Relation"
#    Then this fragment only has 1 "Relation" relation
#    And this fragment is immutable
#    When I fetch the next fragment through the first "Relation"
#    And this fragment contains <restCount> members
#    And this fragment is mutable
#    And this fragment has no relations
#    Examples:
#      | eventStreamDescriptionFile                                      | viewDescriptionFile                                                        | template                                   | collection            | ingestedMemberCount | restCount |
#      | "data/input/eventstreams/fragmentation/mobility-hindrances.ttl" | "data/input/eventstreams/fragmentation/mobility-hindrances.view.paged.ttl" | "data/input/members/mob-hind.template.ttl" | "mobility-hindrances" | 617                 | 17        |

  @geospatial
  Scenario Outline: Server Can Geospatially Fragment an LDES
    Given I create the eventstream <eventStreamDescriptionFile>
    And I ingest 6 members of template <template> to the collection <collection>
    And the LDES <collection> contains <expectedMemberCount> members
    And I wait until all members are fragmented
    Then all members of <collection> are marked as fragmented
    When I fetch the root "by-loc" fragment of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has 3 "GeospatiallyContainsRelation" relation
    And this fragment is mutable
    When I fetch the "tile" fragment for "9/262/171" from the "by-loc" view of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains <expectedMemberCount> members
    When I fetch the "tile" fragment for "9/262/170" from the "by-loc" view of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains <expectedMemberCount> members
    When I fetch the "tile" fragment for "9/261/171" from the "by-loc" view of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains <expectedMemberCount> members
    Examples:
      | eventStreamDescriptionFile                                             | template                                           | collection            | expectedMemberCount |
      | "data/input/eventstreams/fragmentation/mobility-hindrances.by-loc.ttl" | "data/input/members/mob-hind.template.ttl"         | "mobility-hindrances" | 6                   |
      | "data/input/eventstreams/fragmentation/observations/by-loc.ttl"        | "data/input/members/two-observations.template.ttl" | "observations"        | 12                  |

  @timebased
  Scenario Outline: Server can do Timebased Fragmentation of an LDES
    Given I create the eventstream <eventStreamDescriptionFile>
    And I ingest 5 members of template <template> to the collection <collection>
    And the LDES <collection> contains <ingestedMembers> members
    And I wait until all members are fragmented
    Then all members of <collection> are marked as fragmented
    When I fetch the timebased fragment "by-time" fragment of this month of <collection>
    And I fetch the next fragment through the first "GreaterThanOrEqualToRelation"
    And I fetch the next fragment through the first "Relation"
    Examples:
      | eventStreamDescriptionFile                                              | template                                               | collection            | ingestedMembers |
      | "data/input/eventstreams/fragmentation/mobility-hindrances.by-time.ttl" | "data/input/members/mob-hind.template.ttl"             | "mobility-hindrances" | 5               |
      | "data/input/eventstreams/fragmentation/mobility-hindrances.by-time.ttl" | "data/input/members/mob-hind.string-time.template.ttl" | "mobility-hindrances" | 5               |
      | "data/input/eventstreams/fragmentation/mobility-hindrances.by-time.ttl" | "data/input/members/mob-hind.string-time.template.ttl" | "mobility-hindrances" | 5               |
      | "data/input/eventstreams/fragmentation/observations/by-time.ttl"        | "data/input/members/two-observations.template.ttl"     | "observations"        | 10              |

  @reference
  Scenario Outline: Server Can Fragment an LDES using the Reference Fragmentation strategy
    Given I create the eventstream <eventStreamDescriptionFile>
    And I ingest 6 members of template <memberTemplate> to the collection <collection>
    And the LDES <collection> contains <ingestedMemberCount> members
    And I wait until all members are fragmented
    Then all members of <collection> are marked as fragmented
    When I fetch the root "by-ref" fragment of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has <numberOfEqualToRelations> "EqualToRelation" relation
    And this fragment is mutable
    When I fetch the "reference" fragment for <fragmentValue> from the "by-ref" view of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains 6 members
    Examples:
      | eventStreamDescriptionFile                                             | memberTemplate                                     | collection            | ingestedMemberCount | numberOfEqualToRelations | fragmentValue                                                                                         |
      | "data/input/eventstreams/fragmentation/mobility-hindrances.by-ref.ttl" | "data/input/members/mob-hind.template.ttl"         | "mobility-hindrances" | 6                   | 1                        | 'http%3A%2F%2Ftest-data%2Fmobility-hindrance%2F1'                                                     |
      | "data/input/eventstreams/fragmentation/observations/by-ref.ttl"        | "data/input/members/two-observations.template.ttl" | "observations"        | 12                  | 2                        | "https%3A%2F%2Fgeomobility.eu%2Fid%2Fobservation%2F3145a8ea-0f1e-4083-a270-cb18f3d85328%2FB-A%2FOGV1" |

  @nested-reference
  Scenario Outline: Server Can Fragment an LDES using nested Reference Fragmentation strategies
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 2 members of template <firstTemplate> to the collection <collection>
    And I ingest 5 members of template <secondTemplate> to the collection <collection>
    Then the LDES <collection> contains <expectedMemberCount> members
    And I wait until all members are fragmented
    Then all members of <collection> are marked as fragmented
    When I fetch the root "by-nested-ref" fragment of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has <rootEqualToRelationCount> "EqualToRelation" relation
    And this fragment is mutable
    When I fetch the "type" fragment for <firstFragmentValue> from the "by-nested-ref" view of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has <equalToRelationCount> "EqualToRelation" relation
    And this fragment is mutable
    When I fetch the <firstPath> fragment
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains <firstFragmentMemberCount> members
    When I fetch the "type" fragment for <secondFragmentValue> from the "by-nested-ref" view of <collection>
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    Then this fragment only has <equalToRelationCount> "EqualToRelation" relation
    And this fragment is mutable
    When I fetch the <secondPath> fragment
    Then this fragment only has 1 "Relation" relation
    When I fetch the next fragment through the first "Relation"
    And this fragment contains <secondFragmentMemberCount> members
    Examples:
      | eventStreamDescriptionFile                                                    | firstTemplate                                      | secondTemplate                                     | collection            | expectedMemberCount | equalToRelationCount | rootEqualToRelationCount | firstFragmentValue                                                                              | firstPath                                                                                                                                                                                                                                    | firstFragmentMemberCount | secondFragmentValue                                                                             | secondPath                                                                                                                                                                                                                                   | secondFragmentMemberCount |
      | "data/input/eventstreams/fragmentation/mobility-hindrances.by-nested-ref.ttl" | "data/input/members/mob-hind.template.ttl"         | "data/input/members/activity.template.ttl"         | "mobility-hindrances" | 7                   | 1                    | 2                        | 'https%3A%2F%2Fdata.vlaanderen.be%2Fns%2Fmobiliteit%23Mobiliteitshinder'                        | "/mobility-hindrances/by-nested-ref?type=https%3A%2F%2Fdata.vlaanderen.be%2Fns%2Fmobiliteit%23Mobiliteitshinder&version=http%3A%2F%2Ftest-data%2Fmobility-hindrance%2F1"                                                                     | 2                        | 'https%3A%2F%2Fwww.w3.org%2Fns%2Factivitystreams%23Create'                                      | "/mobility-hindrances/by-nested-ref?type=https%3A%2F%2Fwww.w3.org%2Fns%2Factivitystreams%23Create&version=https%3A%2F%2Fwww.w3.org%2Fns%2Factivitystreams%231"                                                                               | 5                         |
      | "data/input/eventstreams/fragmentation/observations/by-nested-ref.ttl"        | "data/input/members/two-observations.template.ttl" | "data/input/members/two-observations.template.ttl" | "observations"        | 14                  | 2                    | 1                        | "https%3A%2F%2Fimplementatie.data.vlaanderen.be%2Fns%2Fvsds-verkeersmetingen%23Verkeerstelling" | "/observations/by-nested-ref?type=https%3A%2F%2Fimplementatie.data.vlaanderen.be%2Fns%2Fvsds-verkeersmetingen%23Verkeerstelling&version=https%3A%2F%2Fgeomobility.eu%2Fid%2Fobservation%2F3145a8ea-0f1e-4083-a270-cb18f3d85328%2FB-A%2FOGV1" | 7                        | "https%3A%2F%2Fimplementatie.data.vlaanderen.be%2Fns%2Fvsds-verkeersmetingen%23Verkeerstelling" | "/observations/by-nested-ref?type=https%3A%2F%2Fimplementatie.data.vlaanderen.be%2Fns%2Fvsds-verkeersmetingen%23Verkeerstelling&version=https%3A%2F%2Fgeomobility.eu%2Fid%2Fobservation%2F3145a8ea-0f1e-4083-a270-cb18f3d85328%2FB-A%2FOGV1" | 7                         |

  @multi-view
  Scenario Outline: Server Allows Multiple Views in an LDES
    Given I create the eventstream <eventStreamDescriptionFile>
    And I ingest 6 members of template <template> to the collection <collection>
    And I wait until all members are fragmented
    Then all members of <collection> are marked as fragmented
    When I fetch the root "by-loc" fragment of <collection>
    And I fetch the next fragment through the first "Relation"
    Then this fragment only has 3 "GeospatiallyContainsRelation" relation
    When I fetch the root "by-loc" fragment of <collection>
    Then this fragment only has 1 "Relation" relation
    Examples:
      | eventStreamDescriptionFile                                             | template                                           | collection            |
      | "data/input/eventstreams/fragmentation/mobility-hindrances.by-loc.ttl" | "data/input/members/mob-hind.template.ttl"         | "mobility-hindrances" |
      | "data/input/eventstreams/fragmentation/observations/by-loc.ttl"        | "data/input/members/two-observations.template.ttl" | "observations"        |

  Scenario Outline: Server can close an event stream
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 317 members of template <template> to the collection <collection>
    And I wait until all members are fragmented
    Then all members of <collection> are marked as fragmented
    Then the following fragment URL <fragmentUrl> contains member with ID <memberId>
    When I close the collection <collection>
    And I fetch the root "paged" fragment of <collection>
    Then this fragment is immutable
    When I fetch the next fragment through the first "Relation"
    Then this fragment is immutable
    When I fetch the next fragment through the first "Relation"
    Then this fragment is immutable

    Examples:
      | eventStreamDescriptionFile                                            | template                                   | collection            | fragmentUrl                               | memberId                                    |
      | "data/input/eventstreams/fragmentation/mobility-hindrances.paged.ttl" | "data/input/members/mob-hind.template.ttl" | "mobility-hindrances" | "/mobility-hindrances/paged?pageNumber=2" | "http://test-data/mobility-hindrance/1/316" |