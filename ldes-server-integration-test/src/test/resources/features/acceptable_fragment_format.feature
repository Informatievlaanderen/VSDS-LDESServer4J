Feature: The LDES server has a fixed naming strategy

  Scenario Outline:
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    Then I can fetch the TreeNode <treeNodeUrl> using content-type <contentType>
    Then I delete the eventstream "mobility-hindrances"

    Examples:
      | treeNodeUrl                  | contentType           |
      | /mobility-hindrances/paged | text/turtle           |
      | /mobility-hindrances/paged | application/n-quads   |
      | /mobility-hindrances/paged | application/ld+json   |
      | /mobility-hindrances/paged | application/n-triples |


