module ldes.server.maintenance.common {
	requires spring.context;
	requires spring.batch.core;
	requires ldes.domain;
	exports be.vlaanderen.informatievlaanderen.ldes.server.maintenance.services;
}