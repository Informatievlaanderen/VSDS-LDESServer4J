module admin {
    requires spring.web;
    requires org.apache.jena.core;
    requires org.apache.tomcat.embed.core;
    requires spring.context;
    requires org.apache.jena.arq;

    requires ldes.domain;
    requires spring.boot;
    requires org.slf4j;
    requires io.swagger.v3.oas.annotations;

}