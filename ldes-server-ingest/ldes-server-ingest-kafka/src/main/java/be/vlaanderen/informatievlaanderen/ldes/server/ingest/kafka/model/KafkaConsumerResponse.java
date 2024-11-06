package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.model;

import java.util.List;

public record KafkaConsumerResponse(String listenerId, String groupId, Boolean active, List<KafkaConsumerAssignment> assignments) {
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String listenerId;
		private String groupId;
		private Boolean active;
		private List<KafkaConsumerAssignment> assignments;

		public Builder listenerId(String listenerId) {
			this.listenerId = listenerId;
			return this;
		}

		public Builder groupId(String groupId) {
			this.groupId = groupId;
			return this;
		}

		public Builder active(Boolean active) {
			this.active = active;
			return this;
		}

		public Builder assignments(List<KafkaConsumerAssignment> assignments) {
			this.assignments = assignments;
			return this;
		}

		public KafkaConsumerResponse build() {
			return new KafkaConsumerResponse(listenerId, groupId, active, assignments);
		}
	}
}
