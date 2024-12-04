open module ldes.fragmentation.domain {
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
	requires spring.core;
	requires spring.boot;
	exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;
}