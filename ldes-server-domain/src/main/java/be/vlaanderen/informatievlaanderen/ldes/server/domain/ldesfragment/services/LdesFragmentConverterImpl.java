package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
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
        ldesMemberRepository.getLdesMembersByIds(ldesFragment.getMemberIds())
                .map(LdesMember::getModel)
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
        statements.add(createStatement(viewId, TREE_SHAPE, createResource(ldesConfig.getShape())));
        statements
                .add(createStatement(viewId, LDES_VERSION_OF, createResource(VERSION_OF_URI)));
        statements.add(createStatement(viewId, LDES_TIMESTAMP_PATH,
                createResource(PROV_GENERATED_AT_TIME)));
        statements.add(createStatement(viewId, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
        return statements;
    }

    private List<Statement> getRelationStatements(LdesFragment ldesFragment, Resource currrentFragmentId) {
        return ldesFragment
                .getRelations()
                .stream()
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
        statements.add(createStatement(treeRelationNode, TREE_VALUE, createTypedLiteral(treeRelation.getTreeValue(), TypeMapper.getInstance().getTypeByName(treeRelation.getTreeValueType()))));
        statements.add(createStatement(treeRelationNode, TREE_PATH, createResource(treeRelation.getTreePath())));
        statements.add(createStatement(treeRelationNode, TREE_NODE, createResource(treeRelation.getTreeNode())));
        statements
                .add(createStatement(treeRelationNode, RDF_SYNTAX_TYPE, createResource(treeRelation.getRelation())));
        return statements;
    }
}
