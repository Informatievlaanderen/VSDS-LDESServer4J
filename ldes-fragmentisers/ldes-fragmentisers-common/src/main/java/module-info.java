open module ldes.fragmentation.domain {
    requires ldes.ingest.domain;
    requires ldes.server.retention;
    requires ldes.domain;
    requires spring.batch.core;
    requires spring.batch.infrastructure;
    requires micrometer.observation;
    requires spring.context;
    requires org.apache.jena.core;
    requires spring.beans;
    requires micrometer.core;
    requires org.slf4j;
    requires org.apache.jena.arq;
    requires org.jetbrains.annotations;
	requires spring.tx;
	exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

}