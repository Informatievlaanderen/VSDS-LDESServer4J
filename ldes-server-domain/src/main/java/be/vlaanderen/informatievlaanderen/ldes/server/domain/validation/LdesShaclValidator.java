package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import org.apache.jena.graph.Graph;

public interface LdesShaclValidator {
	boolean validate(Graph graph);
}
