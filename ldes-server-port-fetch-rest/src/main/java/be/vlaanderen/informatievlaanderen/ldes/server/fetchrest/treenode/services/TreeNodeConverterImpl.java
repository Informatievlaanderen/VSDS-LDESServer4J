package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;

	public TreeNodeConverterImpl(PrefixAdder prefixAdder) {
		this.prefixAdder = prefixAdder;
	}

	@Override
	public Model toModel(final TreeNodeDto treeNodeDto) {
		Model model = ModelFactory.createDefaultModel()
				.add(addTreeNodeStatements(treeNodeDto));
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNodeDto treeNodeDto) {
		return new ArrayList<>(treeNodeDto.getModel().listStatements().toList());
	}
}
