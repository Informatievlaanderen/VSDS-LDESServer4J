open module ldes.admin {

    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

    // LDES dependencies
    requires ldes.domain;

    // external dependencies
    requires spring.web;
    requires org.apache.jena.core;
    requires org.apache.tomcat.embed.core;
    requires spring.context;
    requires org.apache.jena.arq;
    requires spring.boot;
    requires org.slf4j;
    requires io.swagger.v3.oas.annotations;
    requires spring.beans;
    requires org.apache.jena.shacl;
    requires org.apache.commons.lang3;
    requires spring.webmvc;
    requires org.apache.jena.ext.com.google;
    requires spring.boot.autoconfigure;

}