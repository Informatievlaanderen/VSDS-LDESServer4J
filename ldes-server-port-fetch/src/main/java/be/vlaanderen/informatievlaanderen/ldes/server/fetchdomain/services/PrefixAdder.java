package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.services;

import org.apache.jena.rdf.model.Model;

public interface PrefixAdder {
	Model addPrefixesToModel(Model model);
}
