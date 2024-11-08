package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.kafkasource;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;

import java.util.List;

public interface KafkaSourceRepository {
	void save(KafkaSourceProperties kafkaSource);
	void delete(String id);
	List<KafkaSourceProperties> getAll();
}
