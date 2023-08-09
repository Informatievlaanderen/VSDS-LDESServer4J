package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
import org.apache.jena.rdf.model.Model;

public interface TreeNodeConverter {
	Model toModel(final TreeNodeDto treeNodeDto);
}
