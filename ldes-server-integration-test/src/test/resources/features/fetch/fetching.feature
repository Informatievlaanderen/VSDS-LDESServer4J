Feature: Server basic fetching functionality

  Scenario Outline: The LDES server has access control headers, vary header and an etag
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 1 members of template <template> to the collection "<collectionName>"
    Then The response from requesting the url "/<collectionName>" has access control headers, vary header and an etag
    And The response from requesting the url "/<collectionName>/paged" has access control headers, vary header and an etag
    And The response from requesting the url "/<collectionName>/paged?pageNumber=1" has access control headers, vary header and an etag
    And I delete the eventstream <collectionName>
    Examples:
      | eventStreamDescriptionFile                                       | template                                           | collectionName      |
      | "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl" | "data/input/members/mob-hind.template.ttl"         | mobility-hindrances |
      | "data/input/eventstreams/observations.ttl"                       | "data/input/members/two-observations.template.ttl" | observations        |

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
      | eventStreamDescription                                         | collectionEndpoint   | viewEndpoint               | collectionName      |
      | data/input/eventstreams/mobility-hindrances_paginated_1500.ttl | /mobility-hindrances | /mobility-hindrances/paged | mobility-hindrances |
      | data/input/eventstreams/cartoons_paginated_2.ttl               | /cartoons            | /cartoons/my-view          | cartoons            |

  @setupStreaming
  Scenario: The LDES server supports streaming for fetching
    Given I create the eventstream "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl"
    When I ingest 1 members of template "data/input/members/mob-hind.template.ttl" to the collection "mobility-hindrances"
    When I fetch a fragment from url "/mobility-hindrances/paged" in a streaming way and is equal to the model of "/mobility-hindrances/paged"
    When I fetch a fragment from url "/mobility-hindrances/paged?pageNumber=1" in a streaming way and is equal to the model of "/mobility-hindrances/paged?pageNumber=1"

#  @clearRegistry
#  Scenario Outline: Counter is created and returns number of inserted members
#    Given I create the eventstream <eventStreamDescriptionFile>
#    When I ingest 1 members of template <template> to the collection <collectionName>
#    Then The prometheus value for key "ldes_server_ingested_members_count_total" is <prometheusValue>
#
#    Examples:
#      | eventStreamDescriptionFile                                       | template                                           | collectionName        | prometheusValue |
#      | "data/input/eventstreams/mobility-hindrances_paginated_1500.ttl" | "data/input/members/mob-hind.template.ttl"         | "mobility-hindrances" | "1.0"           |
#      | "data/input/eventstreams/observations.ttl"                       | "data/input/members/two-observations.template.ttl" | "observations"        | "2.0"           |

