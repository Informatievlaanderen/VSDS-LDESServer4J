open module ldes.server.retention {
    requires ldes.domain;
    requires spring.context;
    requires spring.batch.core;
    requires org.slf4j;
    requires org.apache.jena.core;
    requires org.apache.jena.arq;
    requires spring.batch.infrastructure;
    requires ldes.server.maintenance.common;
    requires spring.tx;
	requires spring.core;
	requires spring.jdbc;
    requires java.sql;
}