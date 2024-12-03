package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.kafkasource;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;

import java.util.List;

public interface KafkaSourceRepository {
	void save(KafkaSourceProperties kafkaSource, Integer eventStreamId);
	List<KafkaSourceProperties> getAll();

	void deleteWithTopic(String topic);
}
