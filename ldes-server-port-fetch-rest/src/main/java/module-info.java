open module ldes.fetch.rest {
    requires ldes.fetch.domain;
    requires spring.web;
    requires org.apache.tomcat.embed.core;
    requires ldes.domain;
    requires spring.webmvc;
    requires io.swagger.v3.oas.annotations;
    requires org.apache.jena.arq;
    requires org.apache.jena.core;
    requires ldes.ingest.domain;
    requires spring.beans;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires org.apache.commons.codec;
    requires ldes.admin;
    requires spring.boot;
}