Feature: event streams can be configured at runtime

  Scenario: get all event streams
    Given a db containing multiple eventstreams
    When the client calls "/admin/api/v1/eventstreams"
    Then the client receives HTTP status 200
    And the client receives a valid list of event streams

  Scenario: get an existing event stream
    Given a db containing one event stream
    When the client calls "/admin/api/v1/eventstreams/name1"
    Then the client receives HTTP status 200
    And the client receives a single event stream

  Scenario: get a non-existing event stream
    Given an empty db
    When the client calls "/admin/api/v1/eventstreams/name1"
    Then the client receives HTTP status 404
    And I verify the event stream retrieval interaction

  Scenario: put a valid event stream
    Given a db which does not contain specified event stream
    When the client posts model from file eventstream/streams/ldes-1.ttl
    Then the client receives HTTP status 201
    And I verify the event stream in the response body to file eventstream/streams-with-dcat/ldes-1.ttl

  Scenario: post an event stream with an invalid view
    Given a db which does not contain specified event stream
    When the client posts model from file ldes-with-duplicate-retention.ttl
    Then the client receives HTTP status 400

  Scenario: post an event stream that creates versions
    Given a db which does not contain specified event stream
    When the client posts model from file eventstream/streams/ldes-create-versions.ttl
    Then the client receives HTTP status 201
    And I verify the event stream in the response body to file eventstream/streams-with-dcat/ldes-create-versions.ttl


  Scenario Outline: put a invalid event stream
    When the client posts model from file <fileName>
    Then the client receives HTTP status 400
    And I verify the absence of interactions
    And I verify the absence of the event stream
    Examples:
      | fileName                          |
      | ldes-without-type.ttl             |
      | malformed-ldes.ttl                |
      | ldes-with-duplicate-retention.ttl |

  Scenario: delete an existing event stream
    Given a db containing one event stream
    When the client deletes the event stream
    Then the client receives HTTP status 200
    And I verify the db interactions

  Scenario: delete an non-existing event stream
    Given an empty db
    When the client deletes the event stream
    Then the client receives HTTP status 404
    And I verify the event stream retrieval interaction