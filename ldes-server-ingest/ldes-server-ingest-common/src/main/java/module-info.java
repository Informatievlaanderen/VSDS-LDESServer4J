module ldes.ingest.domain {
    requires ldes.domain;
    requires spring.context;
    requires org.apache.jena.core;
    requires org.apache.jena.shacl;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires org.apache.jena.arq;
    requires micrometer.core;
    requires micrometer.observation;
	requires spring.core;
	exports be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities;
    exports be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories;
    exports be.vlaanderen.informatievlaanderen.ldes.server.ingest;
}