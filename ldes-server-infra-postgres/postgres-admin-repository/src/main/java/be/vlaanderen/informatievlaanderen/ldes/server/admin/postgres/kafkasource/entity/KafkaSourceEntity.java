package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.kafkasource.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "collection_kafka_sources")
public class KafkaSourceEntity {
	@Id
	@Column(name = "collection")
	private String collection;
	@Column(name = "collection_id")
	private String topic;
	@Column(name = "mime_type")
	private String mimeType;

	public KafkaSourceEntity() {
	}

	public KafkaSourceEntity(String collection, String topic, String mimeType) {
		this.collection = collection;
		this.topic = topic;
		this.mimeType = mimeType;
	}

	public String getCollection() {
		return collection;
	}

	public String getTopic() {
		return topic;
	}

	public String getMimeType() {
		return mimeType;
	}
}
