open module ldes.admin {

	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repository;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.repository;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.services;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.kafkasource;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception;
	exports be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository;

	// LDES dependencies
	requires ldes.domain;

	// external dependencies
	requires spring.boot;
	requires spring.beans;
	requires spring.context;
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
	requires org.jetbrains.annotations;
	requires micrometer.observation;
	requires micrometer.core;

}