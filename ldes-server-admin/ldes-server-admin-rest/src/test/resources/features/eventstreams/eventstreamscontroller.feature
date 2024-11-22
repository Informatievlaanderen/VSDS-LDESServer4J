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
    When the client posts model from file eventstream/streams/ldes.ttl
    Then the client receives HTTP status 201
    And I verify the event stream in the response body to file eventstream/streams-with-dcat/ldes-with-dcat.ttl
    And I verify the event stream is saved to the db

  Scenario: post an event stream with an invalid view
    Given a db which does not contain specified event stream
    When the client posts model from file eventstream/streams/ldes-with-duplicate-retention.ttl
    Then the client receives HTTP status 400

  Scenario: post an event stream that creates versions
    Given a db which does not contain specified event stream
    When the client posts model from file eventstream/streams/ldes-create-versions.ttl
    Then the client receives HTTP status 201
    And I verify the event stream in the response body to file eventstream/streams-with-dcat/ldes-create-versions.ttl
    And I verify the event stream is saved to the db


  Scenario Outline: put a invalid event stream
    When the client posts model from file <fileName>
    Then the client receives HTTP status 400
    And I verify the absence of interactions
    And I verify the absence of the event stream
    Examples:
      | fileName                                              |
      | eventstream/streams/ldes-without-type.ttl             |
      | eventstream/streams/malformed-ldes.ttl                |
      | eventstream/streams/ldes-with-duplicate-retention.ttl |

  Scenario: delete an existing event stream
    Given a db containing one deletable event stream
    When the client deletes the event stream
    Then the client receives HTTP status 200
    And I verify the event stream deletion interaction

  Scenario: delete an non-existing event stream
    Given an empty db
    When the client deletes the event stream
    Then the client receives HTTP status 404
    And I verify the event stream deletion interaction

  Scenario Outline: create an event stream in a skolemization context
    Given an empty db
    When the client posts model from file <fileName>
    Then the client receives HTTP status <expectedStatus>
    And I verify the event stream in the response body to file <expectedResultBody>
    And I verify <verification>
    Examples:
      | fileName                                           | expectedStatus | expectedResultBody                               | verification                                       |
      | eventstream/streams/ldes.ttl                       | 201            | eventstream/streams-with-dcat/ldes-with-dcat.ttl | the saved event stream has no skolemization domain |
      | eventstream/streams/ldes-with-valid-skol-dom.ttl   | 201            | eventstream/streams-with-dcat/ldes-with-dcat.ttl | the saved event stream has a skolemization domain  |
      | eventstream/streams/ldes-with-invalid-skol-dom.ttl | 400            | shacl/invalid-skol-dom-report.ttl                | no event stream has been saved to the db           |

  Scenario: put an event source to an existing event stream
    Given a db containing one event stream
    When the client posts an event source to that event stream
    Then the client receives HTTP status 200

  Scenario: put an event source to an existing event stream
    Given an empty db
    When the client posts an event source to that event stream
    Then the client receives HTTP status 404
