package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class RelationStatementConverterImpl implements RelationStatementConverter {

    private final LdesConfig ldesConfig;

    public RelationStatementConverterImpl(LdesConfig ldesConfig) {
        this.ldesConfig = ldesConfig;
    }

    public List<Statement> getRelationStatements(List<TreeRelation> ldesFragment, Resource currentFragmentId) {
        return ldesFragment.stream()
                .flatMap(treeRelation -> getRelationStatementsOfRelation(currentFragmentId, treeRelation).stream())
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

    private void addStatementIfMeaningful(List<Statement> statements, Resource subject, Property predicate,
                                          String objectContent) {
        if (hasMeaningfulValue(objectContent))
            statements.add(createStatement(subject, predicate, createResource(objectContent)));
    }

    private boolean hasMeaningfulValue(String objectContent) {
        return objectContent != null && !objectContent.equals("");
    }


}
