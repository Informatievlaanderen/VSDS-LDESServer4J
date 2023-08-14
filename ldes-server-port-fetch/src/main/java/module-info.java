module fetch.domain {
    requires ldes.domain;
    requires spring.context;
    requires ingest.domain;
    requires fragmentation.domain;
    requires spring.beans;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;
}