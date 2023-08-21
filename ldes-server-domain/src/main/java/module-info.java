module ldes.domain {

    // TODO TVB: 17/08/23 see if we can move out snapshot package as well
    // TODO TVB: 17/08/23 fetching?? see what we can move out
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;
    // TODO TVB: 17/08/23 check what can be moved out of this
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

    // Events
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

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

}