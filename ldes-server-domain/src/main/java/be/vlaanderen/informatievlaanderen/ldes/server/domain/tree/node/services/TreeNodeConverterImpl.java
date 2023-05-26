package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeRelationResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.IS_PART_OF_PROPERTY;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;
	private final AppConfig appConfig;
	private final EventStreamService eventStreamService;
	private final DcatViewService dcatViewService;

	public TreeNodeConverterImpl(PrefixAdder prefixAdder, AppConfig appConfig,
			EventStreamService eventStreamService, DcatViewService dcatViewService) {
		this.prefixAdder = prefixAdder;
		this.appConfig = appConfig;
		this.eventStreamService = eventStreamService;
		this.dcatViewService = dcatViewService;
	}

	@Override
	public Model toModel(final TreeNode treeNode) {
		Model model = ModelFactory.createDefaultModel();

		EventStreamResponse eventStream = eventStreamService.retrieveEventStream(treeNode.getCollectionName());

		model.add(addTreeNodeStatements(treeNode, eventStream));

		if (!treeNode.getMembers().isEmpty()) {
			String baseUrl = appConfig.getHostName() + "/" + eventStream.getCollection();
			model.add(addEventStreamStatements(treeNode, baseUrl));
			treeNode.getMembers().stream()
					.map(Member::getModel).forEach(model::add);
		}

		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNode treeNode, EventStreamResponse eventStream) {
		List<TreeRelationResponse> treeRelationResponses = treeNode.getRelations().stream()
				.map(treeRelation -> new TreeRelationResponse(treeRelation.treePath(),
						appConfig.getHostName() + treeRelation.treeNode(),
						treeRelation.treeValue(), treeRelation.treeValueType(), treeRelation.relation()))
				.toList();
		TreeNodeInfoResponse treeNodeInfoResponse = new TreeNodeInfoResponse(treeNode.getFragmentId(),
				treeRelationResponses);
		List<Statement> statements = new ArrayList<>(treeNodeInfoResponse.convertToStatements());
		addLdesCollectionStatements(statements, treeNode.isView(), treeNode.getFragmentId(), eventStream);

		return statements;
	}

	private void addLdesCollectionStatements(List<Statement> statements, boolean isView, String currentFragmentId,
			EventStreamResponse eventStream) {
		String baseUrl = appConfig.getHostName() + "/" + eventStream.getCollection();
		Resource collection = createResource(baseUrl);

		if (isView) {
			EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(
					baseUrl,
					eventStream.getTimestampPath(),
					eventStream.getVersionOfPath(),
					null,
					Collections.singletonList(currentFragmentId));
			statements.addAll(eventStreamInfoResponse.convertToStatements());
			statements.addAll(eventStream.getShacl().listStatements().toList());
			addDcatStatements(statements, currentFragmentId, eventStream.getCollection());
		} else {
			statements.add(createStatement(createResource(currentFragmentId), IS_PART_OF_PROPERTY, collection));
		}
	}

	private void addDcatStatements(List<Statement> statements, String currentFragmentId, String collection) {
		ViewName viewName = ViewName.fromString(currentFragmentId.substring(currentFragmentId.indexOf(collection)));
		dcatViewService.findByViewName(viewName)
				.ifPresent(dcatView -> statements.addAll(dcatView.getStatementsWithBase(appConfig.getHostName())));

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

}
