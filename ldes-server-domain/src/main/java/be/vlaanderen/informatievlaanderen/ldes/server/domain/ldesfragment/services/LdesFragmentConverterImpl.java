package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_TIMESTAMP_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_VERSION_OF;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PROV_GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_SHAPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_VALUE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_VIEW;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.VERSION_OF_URI;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

@Component
public class LdesFragmentConverterImpl implements LdesFragmentConverter {

    private final LdesMemberRepository ldesMemberRepository;

    public LdesFragmentConverterImpl(LdesMemberRepository ldesMemberRepository) {
        this.ldesMemberRepository = ldesMemberRepository;
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
        Resource viewId = createResource(ldesFragment.getFragmentInfo().getView());
        Resource currrentFragmentId = createResource(ldesFragment.getFragmentId());

        statements.addAll(getGeneralLdesStatements(ldesFragment, viewId));
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

    private List<Statement> getGeneralLdesStatements(LdesFragment ldesFragment, Resource viewId) {
        List<Statement> statements = new ArrayList<>();
        statements.add(createStatement(viewId, TREE_SHAPE, createResource(ldesFragment.getFragmentInfo().getShape())));
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

    private List<Statement> getRelationStatementsOfRelation(Resource currrentFragmentId, TreeRelation treeRelation) {
        List<Statement> statements = new ArrayList<>();
        Resource treeRelationNode = createResource();
        statements.add(createStatement(currrentFragmentId, TREE_RELATION, treeRelationNode));
        statements.add(createStatement(treeRelationNode, TREE_VALUE, createResource(treeRelation.getTreeValue())));
        statements.add(createStatement(treeRelationNode, TREE_PATH, createResource(treeRelation.getTreePath())));
        statements.add(createStatement(treeRelationNode, TREE_NODE, createResource(treeRelation.getTreeNode())));
        statements
                .add(createStatement(treeRelationNode, RDF_SYNTAX_TYPE, createResource(treeRelation.getRelation())));
        return statements;
    }
}
