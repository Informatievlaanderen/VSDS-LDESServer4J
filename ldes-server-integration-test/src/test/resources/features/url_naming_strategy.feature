Feature: The LDES server has a fixed naming strategy

  Scenario Outline:
    Given I create the eventstream <eventStreamDescription>
    Then I can fetch the TreeNode <collectionEndpoint> using content-type "text/turtle"
    And I can fetch the TreeNode <viewEndpoint> using content-type "text/turtle"
    Then I delete the eventstream <collectionName>

    Examples:
      | eventStreamDescription                                         | collectionEndpoint   | viewEndpoint                 | collectionName      |
      | data/input/eventstreams/mobility-hindrances_paginated_1500.ttl | /mobility-hindrances | /mobility-hindrances/paged   | mobility-hindrances |
      | data/input/eventstreams/cartoons_paginated_2.ttl               | /cartoons            | /cartoons/my-view | cartoons            |

