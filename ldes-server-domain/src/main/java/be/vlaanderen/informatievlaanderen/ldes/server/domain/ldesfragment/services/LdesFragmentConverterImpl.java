package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class LdesFragmentConverterImpl implements LdesFragmentConverter {

	private final MemberRepository memberRepository;
	private final PrefixAdder prefixAdder;
	private final LdesConfig ldesConfig;

	public LdesFragmentConverterImpl(MemberRepository memberRepository, PrefixAdder prefixAdder,
			LdesConfig ldesConfig) {
		this.memberRepository = memberRepository;
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
	}

	public Model toModel(final LdesFragment ldesFragment) {
		Model model = ModelFactory.createDefaultModel();
		model.add(addTreeNodeStatements(ldesFragment));
		if (!ldesFragment.getMemberIds().isEmpty()) {
			model.add(addEventStreamStatements(ldesFragment));
			memberRepository.getLdesMembersByIds(ldesFragment.getMemberIds()).map(Member::getModel)
					.forEach(model::add);
		}
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(LdesFragment ldesFragment) {
		List<Statement> statements = new ArrayList<>();
		Resource currrentFragmentId = createResource(ldesConfig.getHostName() + ldesFragment.getFragmentId());
		statements.add(createStatement(currrentFragmentId, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
		statements.addAll(getRelationStatements(ldesFragment, currrentFragmentId));
		return statements;
	}

	private List<Statement> addEventStreamStatements(LdesFragment ldesFragment) {
		List<Statement> statements = new ArrayList<>();
		Resource viewId = createResource(ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName());
		statements.addAll(getEventStreamStatements(viewId));
		statements.addAll(getMemberStatements(ldesFragment, viewId));
		return statements;
	}

	private List<Statement> getMemberStatements(LdesFragment ldesFragment, Resource viewId) {
		List<Statement> statements = new ArrayList<>();
		ldesFragment.getMemberIds()
				.forEach(memberId -> statements.add(createStatement(viewId, TREE_MEMBER, createResource(memberId))));
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

	private List<Statement> getRelationStatements(LdesFragment ldesFragment, Resource currrentFragmentId) {
		return ldesFragment.getRelations().stream()
				.flatMap(treeRelation -> getRelationStatementsOfRelation(currrentFragmentId, treeRelation).stream())
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
				ldesConfig.getHostName() + treeRelation.treeNode());
		addStatementIfMeaningful(statements, treeRelationNode, RDF_SYNTAX_TYPE, treeRelation.relation());
		return statements;
	}
}
