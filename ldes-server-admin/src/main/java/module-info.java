module admin {
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.services;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.valueobjects;
    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;
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