open module ldes.server.maintenance.common {
	requires spring.context;
	requires spring.batch.core;
	requires ldes.domain;
	requires spring.beans;
	exports be.vlaanderen.informatievlaanderen.ldes.server.maintenance.services;
	exports be.vlaanderen.informatievlaanderen.ldes.server.maintenance.valueobjects;
}