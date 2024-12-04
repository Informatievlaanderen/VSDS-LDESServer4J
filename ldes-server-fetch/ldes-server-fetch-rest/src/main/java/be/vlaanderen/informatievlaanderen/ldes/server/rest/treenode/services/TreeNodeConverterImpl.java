package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.HostNamePrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.UriPrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;
	private final UriPrefixConstructor prefixConstructor;
	private final TreeNodeStatementCreator treeNodeStatementCreator;

	public TreeNodeConverterImpl(PrefixAdder prefixAdder, UriPrefixConstructor prefixConstructor, TreeNodeStatementCreator treeNodeStatementCreator) {
		this.prefixAdder = prefixAdder;
		this.prefixConstructor = prefixConstructor;
		this.treeNodeStatementCreator = treeNodeStatementCreator;
	}

	@Override
	public Model toModel(final TreeNode treeNode) {
		String prefix = prefixConstructor.buildPrefix();
		Model model = ModelFactory.createDefaultModel()
				.add(treeNodeStatementCreator.addTreeNodeStatements(treeNode, treeNode.getCollectionName(), prefix));

		String baseUrl = prefix + "/" + treeNode.getCollectionName();

		if (!treeNode.isView()) {
			model.add(treeNodeStatementCreator.addEventStreamStatements(treeNode, baseUrl));
		}
		if (!treeNode.getMembers().isEmpty()) {
			treeNode.getMembers().stream()
					.map(member -> member.model().add(
							createStatement(createResource(baseUrl), TREE_MEMBER, createResource(member.subject()))
					))
					.forEach(model::add);
		}

		return prefixAdder.addPrefixesToModel(model).setNsPrefixes(createTreeNodePrefixes(treeNode));
	}

	private Map<String, String> createTreeNodePrefixes(TreeNode treeNode) {
		if(prefixConstructor instanceof HostNamePrefixConstructor hostNamePrefixConstructor) {
			return hostNamePrefixConstructor.buildFragmentUri(treeNode.getCollectionName(), treeNode.getFragmentId());
		}
		return Map.of();
	}
}
