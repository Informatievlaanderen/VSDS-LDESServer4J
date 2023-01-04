package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config;

import org.apache.jena.rdf.model.Property;

public record TimebasedFragmentationConfig(Long memberLimit, Property fragmentationProperty) {
}
