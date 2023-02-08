package be.vlaanderen.informatievlaanderen.vsds.config;

import org.apache.jena.rdf.model.Property;

public record PaginationConfig(Long memberLimit, Property fragmentationProperty) {
}
