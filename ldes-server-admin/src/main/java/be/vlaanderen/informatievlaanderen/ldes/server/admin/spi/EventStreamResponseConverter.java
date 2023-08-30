package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import org.apache.jena.rdf.model.Model;

public interface EventStreamResponseConverter {
	EventStreamResponse fromModel(Model model);

	Model toModel(EventStreamResponse eventStreamResponse);
}
