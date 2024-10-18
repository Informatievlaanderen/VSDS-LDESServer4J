open module ldes.fetch.rest {

    requires ldes.admin;
    requires ldes.domain;
    requires ldes.fetch.domain;

    requires spring.web;
    requires spring.beans;
    requires spring.webmvc;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires org.apache.tomcat.embed.core;
    requires org.apache.jena.arq;
    requires org.apache.jena.core;
    requires org.apache.commons.codec;
    requires io.swagger.v3.oas.annotations;
    requires micrometer.observation;
    requires org.slf4j;
    requires org.jetbrains.annotations;

}