Feature: Geo-Spatial Fragmentation

  Background:
    Given a configured geo-spatial bucketising property "http://www.opengis.net/ont/geosparql#asWKT"
    Given a configured max zoom level at 15
    Given a geo-spatial bucketiser with the defined configuration

  Scenario Outline: Can configure geo-spatial bucketiser with a geographic property (WKT format)
    When I bucketise a member "<member>"
    Then the bucketiser calculates a bucket based on "<bucketValue>"

    Examples:
      | member                       | bucketValue |
      | examples/2-geo-properties.nq | null        |

  Scenario: geo-spatial bucketiser adds LDES members to one or more geo-spatial tiles based on the configured geographic property
    When a member contains a polygon spanning multiple tiles
    Then the member buckets contain all these tiles.

  Scenario: Geo-spatial bucketiser does not add buckets to the LDES member content
    When I bucketise a member "2-geo-properties.nq"
    Then it does not store the buckets

  Scenario Outline: Can configure geo-spatial fragmentiser with a max member count per fragment
    Given a configured member limit of <member-amount> per fragment
    And a geo-spatial bucketiser with the defined configuration
    When I bucketise a member "<member>"
    Then I expect 2 fragments to be created
    And first member fragment has <count> members
    And the second member fragment has <member-residue> members

    Examples:
      | member              | count | member-amount | member-residue |
      | 2-geo-properties.nq | 5     | 6             | 1              |


  Scenario Outline: geo-spatial fragmentiser creates a root fragment containing all available tile fragments
    When I bucketise a member "<member>"
    Then I expect <root-count> root fragments, <tile-count> tile fragments and <memberFragment-count> memberFragments
    And I expect the member to appear in all member fragments

    Examples:
      | member                | root-count | tile-count | memberFragment-count |
      | 5-tile-span-member.nq | 1          | 5          | 5                    |
      | 1-tile-span-member.nq | 1          | 1          | 1                    |


  Scenario Outline: geo-spatial fragmentiser creates a tile fragment containing all available fragments per tile
    Given a configured member limit of <max-member-count> per fragment
    And a geo-spatial bucketiser with the defined configuration
    When I bucketise a member "<member1>"
    And I bucketise a member "<member2>"
    Then I expect <root-count> root fragments, <tile-count> tile fragments and <memberFragment-count> memberFragments
    And the fragments contain <max-member-count> members

    Examples:
      | root-count | tile-count | memberFragment-count | max-member-count | member1               | member2               |
      | 1          | 1          | 2                    | 1                | same-tile-member-1.nq | same-tile-member-2.nq |

  Scenario Outline: Can configure geo-spatial bucketiser with a max zoom level
    Given a configured max zoom level at <max-zoom-level>
    And a geo-spatial bucketiser with the defined configuration
    When I bucketise a member "<member>"
    And abc
    Then I expect <root-count> root fragments, <tile-count> tile fragments and <memberFragment-count> memberFragments

    Examples:
      | max-zoom-level | member                   | root-count | tile-count | memberFragment-count |
      | 10             | bounding-box-level-15.nq | 1          | 1          | 1                    |
