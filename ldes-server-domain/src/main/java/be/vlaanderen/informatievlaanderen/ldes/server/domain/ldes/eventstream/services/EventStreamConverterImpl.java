package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventStreamConverterImpl implements EventStreamConverter {

	private final PrefixAdder prefixAdder;
	private final LdesConfig ldesConfig;

	public EventStreamConverterImpl(PrefixAdder prefixAdder, LdesConfig ldesConfig) {
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
	}

	public Model toModel(final EventStreamResponse eventStreamResponse) {
		Model model = ModelFactory.createDefaultModel();
		model.add(addCollectionStatements(eventStreamResponse.eventStreamInfoResponse()));
		model.add(addViewStatements(eventStreamResponse.views()));
		model.add(ldesConfig.getDcat());
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addCollectionStatements(EventStreamInfoResponse eventStreamInfoResponse) {
		return eventStreamInfoResponse.convertToStatements();
	}

	private List<Statement> addViewStatements(List<TreeNodeInfoResponse> views) {
		final List<Statement> statements = new ArrayList<>();
		views.forEach(view -> statements.addAll(view.convertToStatements()));
		return statements;
	}

}
