Feature: LDES Server Kafka Ingestion

  Scenario Outline: The LDES server supports Kafka ingestion
    Given I have a Kafka Container running
    And I create the eventstream <eventStreamFile>
    When I add 5 members of template <templateFile> to the Kafka topic <topic>
    Then the LDES <topic> contains 5 members
    And I delete the eventstream <topic>
    Examples:
      | eventStreamFile                                               | templateFile                                | topic |
      | "data/input/eventstreams/kafka/event-stream_kafka_ttl.ttl"    | "data/input/members/mob-hind.template.ttl"  | "ttl"   |
      | "data/input/eventstreams/kafka/event-stream_kafka_jsonld.ttl" | "data/input/members/mob-hind.template.json" | "jsonld" |
      | "data/input/eventstreams/kafka/event-stream_kafka_nq.ttl"     | "data/input/members/mob-hind.template.nq"   | "nq"    |


  Scenario: The LDES server supports 2 parallel kafka ingestion pipelines
    Given I have a Kafka Container running
    And I create the eventstream "data/input/eventstreams/kafka/event-stream_kafka_ttl.ttl"
    And I create the eventstream "data/input/eventstreams/kafka/event-stream_kafka_nq.ttl"
    When I add 5 members of template "data/input/members/mob-hind.template.ttl" to the Kafka topic "ttl"
    When I add 10 members of template "data/input/members/mob-hind.template.nq" to the Kafka topic "nq"
    Then the LDES "ttl" contains 5 members
    And the LDES "nq" contains 10 members
    And I delete the eventstream "ttl"
    And I delete the eventstream "nq"