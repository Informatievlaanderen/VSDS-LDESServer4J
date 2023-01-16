package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public interface RelationStatementConverter {

    List<Statement> getRelationStatements(List<TreeRelation> ldesFragment, Resource currentFragmentId);

}
