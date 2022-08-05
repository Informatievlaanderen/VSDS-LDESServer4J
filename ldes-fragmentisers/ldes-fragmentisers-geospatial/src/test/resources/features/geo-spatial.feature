Feature: Geo-Spatial Fragmentation

	Scenario: Can convert geographical data into tiles that follow the SlippyMap naming convention
		Given a WKT literal
		When I call the calculation method
		Then the calculation method returns a String[] of tile names

  Scenario Outline: Can configure geo-spatial bucketiser with a geographic property (WKT format)
    Given a geo-spatial bucketiser that bucketises on property "<propertyA>"
    When I bucketise a member containing geographic properties "<propertyA>" and "<propertyB>"
    Then the bucketiser calculates the bucket based on "<propertyA>"
    
    Examples:
    	|          | propertyA | propertyB |
    	| memberA  | bucketA   | null      |
    	| memberAB | bucketA   | bucketB   |
    	| memberBA | bucketA   | bucketB   |
    	| memberB  | null      | bucketB   |

  Scenario: geo-spatial bucketiser adds LDES members to one or more geo-spatial tiles based on the configured geographic property
    Given a geo-spatial bucketiser
    When a member contains a polygon spanning multiple tiles
    Then the member buckets contain all these tiles.

  Scenario: Geo-spatial bucketiser does not add buckets to the LDES member content
    Given a geo-spatial bucketiser
    When it bucketises
    Then it does not store the buckets

  Scenario Outline: Can configure geo-spatial fragmentiser with a max member count per fragment
    Given a geo-spatial bucketiser
    When I configure it to store <count> members per fragment
    And I pass it <member-amount> members belonging to the same tile
    Then I expect 2 fragments to be created
    And first member fragment has <count> members
    And the second member fragment has <member-residue> members

    Examples:
      | count | member-amount | member-residue |
      | 5     | 6             | 1              |


#  Scenario Outline: geo-spatial fragmentiser creates a root fragment containing all available tile fragments
#    Given a geo-spatial bucketiser
#    When I bucketise a member spanning over <member-tile-span-count> tiles
#    Then I expect following fragments (<root-count> root, <tile-count> tile, <memberFragment-count> membersFragment)
#    And I expect the member to appear in all member fragments

#    Examples:
#      | root-count | tile-count | memberFragment-count | member-tile-span-count |
#      | 1          | 5          | 5                    | 5                      |
#      | 1          | 1          | 1                    | 1                      |


#  Scenario Outline: geo-spatial fragmentiser creates a tile fragment containing all available fragments per tile
#    Given a geo-spatial bucketiser
#    And max-member count is <max-member-count>
#    When I bucketise 2 members that appear in the same tile
#    Then I expect following fragments (<root-count> root, <tile-count> tile, <memberFragment-count> membersFragment)
#    And the fragments contain <max-member-count> member

#    Examples:
#      | root-count | tile-count | memberFragment-count |  max-member-count |
#      | 1          | 1          | 2                    |  1                |

#  Scenario Outline: Can configure geo-spatial bucketiser with a max zoom level
#    Given a geo-spatial bucketiser
#    When i configure the max zoom level at <max-zoom-level>
#    When I bucketise a member at the boundingbox of zoom level <higher-member-zoom-level>
#    Then I expect following fragments (1 root, 1 tile, 1 membersFragment)

#    Examples:
#      | max-zoom-level | higher-member-zoom-level |
#      | 10             | 15                       |
