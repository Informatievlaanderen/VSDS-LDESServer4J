package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.kafkasource;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.kafkasource.KafkaSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.kafkasource.entity.KafkaSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.kafkasource.repository.KafkaSourceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KafkaSourcePostgresRepository implements KafkaSourceRepository {
	private final KafkaSourceEntityRepository repository;

	public KafkaSourcePostgresRepository(KafkaSourceEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public void save(KafkaSourceProperties kafkaSource, Integer eventStreamId) {
		repository.save(new KafkaSourceEntity(eventStreamId, kafkaSource.collection(), kafkaSource.topic(), kafkaSource.mimeType()));
	}

	@Override
	public List<KafkaSourceProperties> getAll() {
		return repository.findAll().stream()
				.map(kafkaSourceEntity ->
						new KafkaSourceProperties(kafkaSourceEntity.getCollection(),
								kafkaSourceEntity.getTopic(),
								kafkaSourceEntity.getMimeType()))
				.toList();
	}

	@Override
	public void deleteWithTopic(String topic) {
		repository.deleteByTopic(topic);
	}
}
