package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;
	private final PrefixConstructor prefixConstructor;

	private final Map<ViewName, DcatView> dcatViews = new HashMap<>();
	private final Map<String, EventStream> eventStreams = new HashMap<>();
	private final Map<String, Model> shaclShapes = new HashMap<>();

	public TreeNodeConverterImpl(PrefixAdder prefixAdder, PrefixConstructor prefixConstructor) {
		this.prefixAdder = prefixAdder;
		this.prefixConstructor = prefixConstructor;
	}

	@Override
	public Model toModel(final TreeNode treeNode) {
		String prefix = prefixConstructor.buildPrefix();
		Model model = ModelFactory.createDefaultModel()
				.add(addTreeNodeStatements(treeNode, treeNode.getCollectionName(), prefix));

		if (!treeNode.getMembers().isEmpty()) {
			String baseUrl = prefix + "/" + treeNode.getCollectionName();
			model.add(addEventStreamStatements(treeNode, baseUrl));
			treeNode.getMembers().stream()
					.map(Member::getModel).forEach(model::add);
		}

		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNode treeNode, String collectionName, String prefix) {
		EventStream eventStream = eventStreams.get(collectionName);
		Model shaclShape = shaclShapes.get(collectionName);
		List<TreeRelationResponse> treeRelationResponses = treeNode.getRelations().stream()
				.map(treeRelation -> new TreeRelationResponse(treeRelation.treePath(),
						prefix + treeRelation.treeNode().asEncodedFragmentId(),
						treeRelation.treeValue(), treeRelation.treeValueType(), treeRelation.relation()))
				.toList();
		TreeNodeInfoResponse treeNodeInfoResponse = new TreeNodeInfoResponse(treeNode.getFragmentId(),
				treeRelationResponses);
		List<Statement> statements = new ArrayList<>(treeNodeInfoResponse.convertToStatements());
		addLdesCollectionStatements(statements, treeNode.isView(), treeNode.getFragmentId(), eventStream, shaclShape, treeNode.getNumberOfMembersInView(), prefix);

		return statements;
	}

	private void addLdesCollectionStatements(List<Statement> statements, boolean isView, String currentFragmentId,
			EventStream eventStream, Model shaclShape, long numberOfMembersInView, String prefix) {
		String baseUrl = prefix + "/" + eventStream.getCollection();
		Resource collection = createResource(baseUrl);

		if (isView) {
			EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(
					baseUrl,
					eventStream.getTimestampPath(),
					eventStream.getVersionOfPath(),
					shaclShape,
					Collections.singletonList(currentFragmentId));
			statements.addAll(eventStreamInfoResponse.convertToStatements());
			statements.addAll(shaclShape.listStatements().toList());
			addDcatStatements(statements, currentFragmentId, eventStream.getCollection(), prefix);
			// 04/12/23 Desactivated due to performance issues on the count query
			// refer to: https://github.com/Informatievlaanderen/VSDS-LDESServer4J/issues/1028
//			statements.add(createStatement(createResource(currentFragmentId), createProperty(TREE_REMAINING_ITEMS), createTypedLiteral(numberOfMembersInView)));
		} else {
			statements.add(createStatement(createResource(currentFragmentId), IS_PART_OF_PROPERTY, collection));
		}
	}

	private void addDcatStatements(List<Statement> statements, String currentFragmentId, String collection, String prefix) {
		ViewName viewName = ViewName.fromString(currentFragmentId.substring(currentFragmentId.indexOf(collection)));
		DcatView dcatView = dcatViews.get(viewName);
		if (dcatView != null) {
			statements.addAll(dcatView.getStatementsWithBase(prefix));
		}
	}

	private List<Statement> addEventStreamStatements(TreeNode treeNode, String baseUrl) {
		List<Statement> statements = new ArrayList<>();
		Resource viewId = createResource(baseUrl);
		statements.addAll(getEventStreamStatements(viewId));
		statements.addAll(getMemberStatements(treeNode, viewId));
		return statements;
	}

	private List<Statement> getMemberStatements(TreeNode treeNode, Resource viewId) {
		List<Statement> statements = new ArrayList<>();
		treeNode.getMembers()
				.stream().map(Member::getMemberIdWithoutPrefix)
				.forEach(memberId -> statements.add(createStatement(viewId, TREE_MEMBER,
						createResource(memberId))));
		return statements;
	}

	private List<Statement> getEventStreamStatements(Resource viewId) {
		List<Statement> statements = new ArrayList<>();
		statements.add(createStatement(viewId, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
		return statements;
	}

	@EventListener
	public void handleEventStreamInitEvent(EventStreamCreatedEvent event) {
		eventStreams.put(event.eventStream().getCollection(), event.eventStream());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		eventStreams.remove(event.collectionName());
	}

	@EventListener
	public void handleShaclInitEvent(ShaclChangedEvent event) {
		shaclShapes.put(event.getCollection(), event.getModel());
	}

	@EventListener
	public void handleShaclDeletedEvent(ShaclDeletedEvent event) {
		shaclShapes.remove(event.collectionName());
	}

	@EventListener
	public void handleDcatViewSavedEvent(DcatViewSavedEvent event) {
		dcatViews.put(event.dcatView().getViewName(), event.dcatView());
	}

	@EventListener
	public void handleDcatViewDeletedEvent(DcatViewDeletedEvent event) {
		dcatViews.remove(event.viewName());
	}

}
