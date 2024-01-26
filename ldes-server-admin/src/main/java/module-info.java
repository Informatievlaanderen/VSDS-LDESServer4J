open module ldes.admin {

    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

    // LDES dependencies
    requires ldes.domain;

    // external dependencies
    requires spring.boot;
    requires spring.beans;
    requires spring.context;
    requires spring.web;
    requires spring.webmvc;
    requires spring.boot.autoconfigure;
    requires spring.boot.actuator;
    requires spring.boot.actuator.autoconfigure;
    requires org.apache.commons.lang3;
    requires org.apache.tomcat.embed.core;
    requires org.apache.jena.arq;
    requires org.apache.jena.core;
    requires org.apache.jena.shacl;
    requires com.google.common;
    requires org.slf4j;
    requires io.swagger.v3.oas.annotations;
    requires org.jetbrains.annotations;
	requires micrometer.observation;

}