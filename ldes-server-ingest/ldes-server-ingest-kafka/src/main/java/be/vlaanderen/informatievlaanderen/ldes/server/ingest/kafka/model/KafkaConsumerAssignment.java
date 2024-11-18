package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.model;

public record KafkaConsumerAssignment(String topic, Integer partition) {
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String topic;
		private Integer partition;

		public Builder topic(String topic) {
			this.topic = topic;
			return this;
		}

		public Builder partition(Integer partition) {
			this.partition = partition;
			return this;
		}

		public KafkaConsumerAssignment build() {
			return new KafkaConsumerAssignment(topic, partition);
		}
	}
}
