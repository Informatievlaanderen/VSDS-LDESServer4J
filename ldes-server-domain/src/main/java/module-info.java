open module ldes.domain {

    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.model;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.encodig;

    // Events
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;
	exports be.vlaanderen.informatievlaanderen.ldes.server.domain.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.domain.collections;

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
    requires simpleclient;
    requires micrometer.core;
    requires org.apache.tomcat.embed.core;
    requires titanium.json.ld;
	requires spring.boot.autoconfigure;

}