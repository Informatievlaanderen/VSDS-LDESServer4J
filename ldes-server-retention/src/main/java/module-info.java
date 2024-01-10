module ldes.server.retention {
	requires ldes.domain;
	requires spring.context;
	requires org.slf4j;
	requires org.apache.jena.core;
	requires org.apache.jena.arq;
	exports be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;
	exports be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;
	exports be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects;
	exports be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;
	exports be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition;
}