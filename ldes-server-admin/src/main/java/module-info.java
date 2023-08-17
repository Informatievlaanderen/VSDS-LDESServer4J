module admin {
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.valueobjects;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities;

    opens be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;
//    opens be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config to spring.core;
    opens be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers to spring.core;
    opens be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling to spring.core;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities;
    requires spring.web;
    requires org.apache.jena.core;
    requires org.apache.tomcat.embed.core;
    requires spring.context;
    requires org.apache.jena.arq;
    requires ldes.domain;
    requires spring.boot;
    requires org.slf4j;
    requires io.swagger.v3.oas.annotations;
    requires spring.beans;
    requires org.apache.jena.shacl;
    requires org.apache.commons.lang3;
    requires spring.webmvc;
    requires org.apache.jena.ext.com.google;

}