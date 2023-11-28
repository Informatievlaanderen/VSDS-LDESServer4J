open module ldes.admin {

    exports be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

    // LDES dependencies
    requires ldes.domain;
    requires ldes.ingest.domain;
    requires ldes.fragmentation.domain;

    // external dependencies
    requires spring.boot;
    requires spring.beans;
    requires spring.context;
    requires spring.web;
    requires spring.webmvc;
    requires spring.boot.autoconfigure;
    requires org.apache.commons.lang3;
    requires org.apache.tomcat.embed.core;
    requires org.apache.jena.arq;
    requires org.apache.jena.core;
    requires org.apache.jena.shacl;
    requires org.apache.jena.ext.com.google;
    requires org.slf4j;
    requires io.swagger.v3.oas.annotations;
    requires org.jetbrains.annotations;
	requires micrometer.observation;

}