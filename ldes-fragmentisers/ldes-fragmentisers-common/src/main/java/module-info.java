module ldes.fragmentation.domain {
    requires ldes.ingest.domain;
    requires ldes.domain;
    requires micrometer.observation;
    requires spring.context;
    requires org.apache.jena.core;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

}