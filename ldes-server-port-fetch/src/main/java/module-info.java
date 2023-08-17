module ldes.fetch.domain {
    requires ldes.domain;
    requires spring.context;
    requires ldes.ingest.domain;
    requires ldes.fragmentation.domain;
    requires spring.beans;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;
}