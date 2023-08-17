module ldes.domain {
    requires org.apache.jena.core;
    requires org.slf4j;
    requires spring.context;
    requires org.apache.jena.arq;
    requires spring.web;
    requires spring.beans;
    requires org.apache.commons.lang3;
    requires org.apache.jena.shacl;
    requires spring.boot;
    requires spring.core;
    requires spring.data.commons;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.model;
}