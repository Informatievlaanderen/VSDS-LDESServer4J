package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE_RESOURCE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_VALUE;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
<<<<<<< HEAD:ldes-server-domain/src/main/java/be/vlaanderen/informatievlaanderen/ldes/server/domain/tree/node/services/TreeNodeConverterImpl.java
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;
=======
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
>>>>>>> 2d09afb (feat: VSDSPUB-110: Url strategy):ldes-server-domain/src/main/java/be/vlaanderen/informatievlaanderen/ldes/server/domain/ldesfragment/services/LdesFragmentConverterImpl.java

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;
	private final LdesConfig ldesConfig;

	public TreeNodeConverterImpl(PrefixAdder prefixAdder,
			LdesConfig ldesConfig) {
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
	}

	@Override
	public Model toModel(final TreeNode treeNode) {
		Model model = ModelFactory.createDefaultModel();
<<<<<<< HEAD:ldes-server-domain/src/main/java/be/vlaanderen/informatievlaanderen/ldes/server/domain/tree/node/services/TreeNodeConverterImpl.java
		model.add(addTreeNodeStatements(treeNode));
		if (!treeNode.getMembers().isEmpty()) {
			model.add(addEventStreamStatements(treeNode));
			treeNode.getMembers().stream()
					.map(Member::getModel)
					.forEach(model::add);
=======
		model.add(addTreeNodeStatements(ldesFragment));
		if (!ldesFragment.getMemberIds().isEmpty()) {
			model.add(addEventStreamStatements(ldesFragment));
			memberRepository.getLdesMembersByIds(ldesFragment.getMemberIds()).map(Member::getModel).forEach(model::add);
>>>>>>> 2d09afb (feat: VSDSPUB-110: Url strategy):ldes-server-domain/src/main/java/be/vlaanderen/informatievlaanderen/ldes/server/domain/ldesfragment/services/LdesFragmentConverterImpl.java
		}
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNode treeNode) {
		List<Statement> statements = new ArrayList<>();
<<<<<<< HEAD:ldes-server-domain/src/main/java/be/vlaanderen/informatievlaanderen/ldes/server/domain/tree/node/services/TreeNodeConverterImpl.java
		Resource currentFragmentId = createResource(ldesConfig.getHostName() + treeNode.getFragmentId());
		statements.add(createStatement(currentFragmentId, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
		statements.addAll(getRelationStatements(treeNode.getRelations(), currentFragmentId));
=======
		Resource currrentFragmentId = createResource(
				ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName() + ldesFragment.getFragmentId());
		statements.add(createStatement(currrentFragmentId, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
		statements.addAll(getRelationStatements(ldesFragment, currrentFragmentId));
>>>>>>> bb7e189 (feat: VSDSPUB-110: url strategy: Added tests):ldes-server-domain/src/main/java/be/vlaanderen/informatievlaanderen/ldes/server/domain/ldesfragment/services/LdesFragmentConverterImpl.java
		return statements;
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

	private void addStatementIfMeaningful(List<Statement> statements, Resource subject, Property predicate,
			String objectContent) {
		if (hasMeaningfulValue(objectContent))
			statements.add(createStatement(subject, predicate, createResource(objectContent)));
	}

	private boolean hasMeaningfulValue(String objectContent) {
		return objectContent != null && !objectContent.equals("");
	}

	private List<Statement> getRelationStatements(List<TreeRelation> ldesFragment, Resource currentFragmentId) {
		return ldesFragment.stream()
				.flatMap(treeRelation -> getRelationStatementsOfRelation(currentFragmentId,
						treeRelation).stream())
				.toList();
	}

	private List<Statement> getRelationStatementsOfRelation(Resource currentFragmentId, TreeRelation treeRelation) {
		List<Statement> statements = new ArrayList<>();
		Resource treeRelationNode = createResource();
		statements.add(createStatement(currentFragmentId, TREE_RELATION, treeRelationNode));
		if (hasMeaningfulValue(treeRelation.treeValue()))
			statements.add(createStatement(treeRelationNode, TREE_VALUE, createTypedLiteral(treeRelation.treeValue(),
					TypeMapper.getInstance().getTypeByName(treeRelation.treeValueType()))));
		addStatementIfMeaningful(statements, treeRelationNode, TREE_PATH, treeRelation.treePath());
		addStatementIfMeaningful(statements, treeRelationNode, TREE_NODE,
				ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName() + treeRelation.treeNode());
		addStatementIfMeaningful(statements, treeRelationNode, RDF_SYNTAX_TYPE, treeRelation.relation());
		return statements;
	}
}
