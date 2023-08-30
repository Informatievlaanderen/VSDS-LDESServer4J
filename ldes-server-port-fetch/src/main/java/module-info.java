module ldes.fetch.domain {

    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.exceptions;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;
    exports be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

    requires ldes.domain;
    requires ldes.ingest.domain;
    requires ldes.fragmentation.domain;

    requires spring.context;
    requires spring.beans;

}