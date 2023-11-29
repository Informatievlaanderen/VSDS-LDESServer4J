module ldes.domain {

    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

    // Events
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.compaction;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

    requires spring.web;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.core;
    requires spring.data.commons;
    requires org.apache.jena.core;
    requires org.apache.jena.arq;
    requires org.apache.commons.lang3;
    requires org.apache.jena.shacl;
    requires org.slf4j;
    requires org.apache.tomcat.embed.core;

}