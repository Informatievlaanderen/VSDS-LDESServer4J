package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.HOST_NAME_KEY;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;
	private String hostName;

	private final HashMap<String, EventStream> eventStreams = new HashMap<>();
	private final DcatViewService dcatViewService;

	public TreeNodeConverterImpl(PrefixAdder prefixAdder, @Value(HOST_NAME_KEY) String hostName,
			DcatViewService dcatViewService) {
		this.prefixAdder = prefixAdder;
		this.hostName = hostName;
		this.dcatViewService = dcatViewService;
	}

	@Override
	public Model toModel(final TreeNodeDto treeNodeDto) {
		Model model = ModelFactory.createDefaultModel()
				.add(addTreeNodeStatements(treeNodeDto, treeNodeDto.getCollectionName()));
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNodeDto treeNodeDto, String collectionName) {
		EventStream eventStream = eventStreams.get(collectionName);
		List<Statement> statements = new ArrayList<>(treeNodeDto.getModel().listStatements().toList());
		addLdesCollectionStatements(statements, treeNodeDto.isView(), treeNodeDto.getFragmentId(), eventStream);

		return statements;
	}

	private void addLdesCollectionStatements(List<Statement> statements, boolean isView, String currentFragmentId,
			EventStream eventStream) {
		String baseUrl = hostName + "/" + eventStream.getCollection();

		if (isView) {
			EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(
					baseUrl,
					eventStream.getTimestampPath(),
					eventStream.getVersionOfPath(),
					null,
					Collections.singletonList(currentFragmentId));
			statements.addAll(eventStreamInfoResponse.convertToStatements());
			addDcatStatements(statements, currentFragmentId, eventStream.getCollection());
		}
	}

	private void addDcatStatements(List<Statement> statements, String currentFragmentId, String collection) {
		ViewName viewName = ViewName.fromString(currentFragmentId.substring(currentFragmentId.indexOf(collection)));
		dcatViewService.findByViewName(viewName)
				.ifPresent(dcatView -> statements.addAll(dcatView.getStatementsWithBase(hostName)));

	}

	@EventListener
	public void handleEventStreamInitEvent(EventStreamCreatedEvent event) {
		eventStreams.put(event.eventStream().getCollection(), event.eventStream());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		eventStreams.remove(event.collectionName());
	}
}
