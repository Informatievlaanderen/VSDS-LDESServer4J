module fragmentation.domain {
    requires ingest.domain;
    requires micrometer.observation;
    requires spring.context;
    requires ldes.domain;
    requires org.apache.jena.core;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

}