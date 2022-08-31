package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class LdesFragmentConverterImpl implements LdesFragmentConverter {

	private final LdesMemberRepository ldesMemberRepository;
	private final LdesConfig ldesConfig;

	public LdesFragmentConverterImpl(LdesMemberRepository ldesMemberRepository, LdesConfig ldesConfig) {
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesConfig = ldesConfig;
	}

	public Model toModel(final LdesFragment ldesFragment) {
		Model model = ModelFactory.createDefaultModel();
		model.add(addRelationAndMetaDataStatements(ldesFragment));
		ldesMemberRepository.getLdesMembersByIds(ldesFragment.getMemberIds()).map(LdesMember::getModel)
				.forEach(model::add);
		return model;
	}

	private List<Statement> addRelationAndMetaDataStatements(LdesFragment ldesFragment) {
		List<Statement> statements = new ArrayList<>();
		Resource viewId = createResource(ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName());
		Resource currrentFragmentId = createResource(ldesFragment.getFragmentId());

		statements.addAll(getGeneralLdesStatements(viewId));
		statements.addAll(getViewStatements(ldesFragment, viewId, currrentFragmentId));
		statements.addAll(getRelationStatements(ldesFragment, currrentFragmentId));
		statements.addAll(getMemberStatements(ldesFragment, viewId));
		return statements;
	}

	private List<Statement> getMemberStatements(LdesFragment ldesFragment, Resource viewId) {
		List<Statement> statements = new ArrayList<>();
		ldesFragment.getMemberIds()
				.forEach(memberId -> statements.add(createStatement(viewId, TREE_MEMBER, createResource(memberId))));
		return statements;
	}

	private List<Statement> getGeneralLdesStatements(Resource viewId) {
		List<Statement> statements = new ArrayList<>();
		addStatementIfMeaningful(statements, viewId, TREE_SHAPE, ldesConfig.getShape());
		addStatementIfMeaningful(statements, viewId, LDES_VERSION_OF, ldesConfig.getVersionOf());
		addStatementIfMeaningful(statements, viewId, LDES_TIMESTAMP_PATH, ldesConfig.getTimestampPath());
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

	private List<Statement> getRelationStatements(LdesFragment ldesFragment, Resource currrentFragmentId) {
		return ldesFragment.getRelations().stream()
				.flatMap(treeRelation -> getRelationStatementsOfRelation(currrentFragmentId, treeRelation).stream())
				.toList();
	}

	private List<Statement> getViewStatements(LdesFragment ldesFragment, Resource viewId, Resource currrentFragmentId) {
		if (ldesFragment.isExistingFragment())
			return List.of(createStatement(viewId, TREE_VIEW, currrentFragmentId));
		return List.of();
	}

	private List<Statement> getRelationStatementsOfRelation(Resource currentFragmentId, TreeRelation treeRelation) {
		List<Statement> statements = new ArrayList<>();
		Resource treeRelationNode = createResource();
		statements.add(createStatement(currentFragmentId, TREE_RELATION, treeRelationNode));
		if (hasMeaningfulValue(treeRelation.getTreeValue()))
			statements.add(createStatement(treeRelationNode, TREE_VALUE, createTypedLiteral(treeRelation.getTreeValue(),
					TypeMapper.getInstance().getTypeByName(treeRelation.getTreeValueType()))));
		addStatementIfMeaningful(statements, treeRelationNode, TREE_PATH, treeRelation.getTreePath());
		addStatementIfMeaningful(statements, treeRelationNode, TREE_NODE, treeRelation.getTreeNode());
		addStatementIfMeaningful(statements, treeRelationNode, RDF_SYNTAX_TYPE, treeRelation.getRelation());
		return statements;
	}
}
