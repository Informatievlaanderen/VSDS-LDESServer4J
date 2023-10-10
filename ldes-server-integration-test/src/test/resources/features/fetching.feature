Feature: Server basic fetching functionality

  Scenario: The LDES server has access control headers and etag
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest 1 members to the collection "mobility-hindrances"
    Then The response from requesting the url "/mobility-hindrances" has access control headers and an etag
    And The response from requesting the url "/mobility-hindrances/paged" has access control headers and an etag
    And The response from requesting the url "/mobility-hindrances/paged?pageNumber=1" has access control headers and an etag
    And I delete the eventstream "mobility-hindrances"

  Scenario Outline: The LDES server supports different mime types for fetching
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    Then I can fetch the TreeNode <treeNodeUrl> using content-type <contentType>
    And I delete the eventstream "mobility-hindrances"

    Examples:
      | treeNodeUrl                | contentType           |
      | /mobility-hindrances/paged | text/turtle           |
      | /mobility-hindrances/paged | application/n-quads   |
      | /mobility-hindrances/paged | application/ld+json   |
      | /mobility-hindrances/paged | application/n-triples |

  Scenario Outline: The LDES server has a fixed naming strategy
    Given I create the eventstream <eventStreamDescription>
    Then I can fetch the TreeNode <collectionEndpoint> using content-type "text/turtle"
    And I can fetch the TreeNode <viewEndpoint> using content-type "text/turtle"
    And I delete the eventstream <collectionName>

    Examples:
      | eventStreamDescription                                         | collectionEndpoint   | viewEndpoint                 | collectionName      |
      | data/input/eventstreams/mobility-hindrances_paginated_1500.ttl | /mobility-hindrances | /mobility-hindrances/paged   | mobility-hindrances |
      | data/input/eventstreams/cartoons_paginated_2.ttl               | /cartoons            | /cartoons/my-view            | cartoons            |