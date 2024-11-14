Feature: LDES Server Kafka Ingestion

  Scenario Outline: The LDES server supports Kafka ingestion
    Given I have a Kafka Container running
    And I create the eventstream <eventStreamFile>
    When I add 5 members of template <templateFile> to the Kafka topic <topic>
    Then the LDES "event-stream" contains 5 members
    And I delete the eventstream "event-stream"
    Examples:
      | eventStreamFile                                               | templateFile                                | topic |
      | "data/input/eventstreams/kafka/event-stream_kafka_ttl.ttl"    | "data/input/members/mob-hind.template.ttl"  | "ttl"   |
      | "data/input/eventstreams/kafka/event-stream_kafka_jsonld.ttl" | "data/input/members/mob-hind.template.json" | "jsonld" |
      | "data/input/eventstreams/kafka/event-stream_kafka_nq.ttl"     | "data/input/members/mob-hind.template.nq"   | "nq"    |

