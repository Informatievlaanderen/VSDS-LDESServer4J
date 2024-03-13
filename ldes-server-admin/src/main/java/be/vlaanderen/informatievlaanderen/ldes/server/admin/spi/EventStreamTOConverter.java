package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import org.apache.jena.rdf.model.Model;

public interface EventStreamTOConverter {
	EventStreamTO fromModel(Model model);

	Model toModel(EventStreamTO eventStreamTO);
}
