package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;

public record KafkaSourceAddedEvent(KafkaSourceProperties kafkaSource) {
}
