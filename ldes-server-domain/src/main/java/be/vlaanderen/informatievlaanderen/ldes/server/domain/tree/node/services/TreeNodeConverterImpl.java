package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.services.RelationStatementConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;
	private final LdesConfig ldesConfig;
	private final RelationStatementConverter relationStatementConverter;

	public TreeNodeConverterImpl(PrefixAdder prefixAdder,
			LdesConfig ldesConfig, RelationStatementConverter relationStatementConverter) {
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
		this.relationStatementConverter = relationStatementConverter;
	}

	@Override
	public Model toModel(final TreeNode treeNode) {
		Model model = ModelFactory.createDefaultModel();
		model.add(addTreeNodeStatements(treeNode));

		if (!treeNode.getMembers().isEmpty()) {
			model.add(addEventStreamStatements(treeNode));
			treeNode.getMembers().stream()
					.map(Member::getModel).forEach(model::add);
		}

		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNode treeNode) {
		List<Statement> statements = new ArrayList<>();
		Resource currentFragmentId = createResource(treeNode.getFragmentId());

		statements.add(createStatement(currentFragmentId, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
		statements.addAll(relationStatementConverter.getRelationStatements(treeNode.getRelations(), currentFragmentId));

		addLdesCollectionStatements(statements, treeNode.isView(), treeNode.getFragmentId());

		return statements;
	}

	private void addLdesCollectionStatements(List<Statement> statements, boolean isView, String currentFragmentId) {
		Resource collection = createResource(ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName());
		String eventStreamId = ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName();

		if (isView) {
			EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(eventStreamId,
					ldesConfig.getTimestampPath(), ldesConfig.getVersionOfPath(), ldesConfig.validation().getShape(),
					Collections.singletonList(currentFragmentId));
			statements.addAll(eventStreamInfoResponse.convertToStatements());
		} else {
			statements.add(createStatement(createResource(currentFragmentId), IS_PART_OF_PROPERTY, collection));
		}
	}

	private List<Statement> addEventStreamStatements(TreeNode treeNode) {
		List<Statement> statements = new ArrayList<>();
		Resource viewId = createResource(ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName());
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
