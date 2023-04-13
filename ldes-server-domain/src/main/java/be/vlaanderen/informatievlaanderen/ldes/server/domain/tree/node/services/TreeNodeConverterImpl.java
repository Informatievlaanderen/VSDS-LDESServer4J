package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeRelationResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
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

	public TreeNodeConverterImpl(PrefixAdder prefixAdder,
			AppConfig appConfig) {
		this.prefixAdder = prefixAdder;
		this.appConfig = appConfig;
	}

	@Override
	public Model toModel(final TreeNode treeNode) {
		Model model = ModelFactory.createDefaultModel();

		LdesConfig ldesConfig = appConfig.getLdesConfig(treeNode.getCollectionName());

		model.add(addTreeNodeStatements(treeNode, ldesConfig));

		if (!treeNode.getMembers().isEmpty()) {
			model.add(addEventStreamStatements(treeNode, ldesConfig));
			treeNode.getMembers().stream()
					.map(Member::getModel).forEach(model::add);
		}

		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNode treeNode, LdesConfig ldesConfig) {
		List<TreeRelationResponse> treeRelationResponses = treeNode.getRelations().stream()
				.map(treeRelation -> new TreeRelationResponse(treeRelation.treePath(),
						ldesConfig.getBaseUrl() + treeRelation.treeNode(),
						treeRelation.treeValue(), treeRelation.treeValueType(), treeRelation.relation()))
				.toList();
		TreeNodeInfoResponse treeNodeInfoResponse = new TreeNodeInfoResponse(treeNode.getFragmentId(),
				treeRelationResponses);
		List<Statement> statements = new ArrayList<>(treeNodeInfoResponse.convertToStatements());
		addLdesCollectionStatements(statements, treeNode.isView(), treeNode.getFragmentId(), ldesConfig);

		return statements;
	}

	private void addLdesCollectionStatements(List<Statement> statements, boolean isView, String currentFragmentId,
			LdesConfig ldesConfig) {
		Resource collection = createResource(ldesConfig.getBaseUrl());

		if (isView) {
			EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(
					ldesConfig.getBaseUrl(),
					ldesConfig.getTimestampPath(), ldesConfig.getVersionOfPath(),
					ldesConfig.validation().getShape(),
					Collections.singletonList(currentFragmentId));
			statements.addAll(eventStreamInfoResponse.convertToStatements());
		} else {
			statements.add(createStatement(createResource(currentFragmentId), IS_PART_OF_PROPERTY, collection));
		}
	}

	private List<Statement> addEventStreamStatements(TreeNode treeNode, LdesConfig ldesConfig) {
		List<Statement> statements = new ArrayList<>();
		Resource viewId = createResource(ldesConfig.getBaseUrl());
		statements.addAll(getEventStreamStatements(viewId));
		statements.addAll(getMemberStatements(treeNode, viewId));
		return statements;
	}

	private List<Statement> getMemberStatements(TreeNode treeNode, Resource viewId) {
		List<Statement> statements = new ArrayList<>();
		treeNode.getMembers()
				.stream().map(Member::getLdesMemberId)
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
