package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

    private final PrefixAdder prefixAdder;
    private final PrefixConstructor prefixConstructor;
    private final TreeNodeStatementCreator treeNodeStatementCreator;

    public TreeNodeConverterImpl(PrefixAdder prefixAdder, PrefixConstructor prefixConstructor, TreeNodeStatementCreator treeNodeStatementCreator) {
        this.prefixAdder = prefixAdder;
        this.prefixConstructor = prefixConstructor;
        this.treeNodeStatementCreator = treeNodeStatementCreator;
    }

    @Override
    public Model toModel(final TreeNode treeNode) {
        String prefix = prefixConstructor.buildPrefix();
        Model model = ModelFactory.createDefaultModel()
                .add(treeNodeStatementCreator.addTreeNodeStatements(treeNode, treeNode.getCollectionName(), prefix));

        if (!treeNode.isView()) {
            String baseUrl = prefix + "/" + treeNode.getCollectionName();
            model.add(treeNodeStatementCreator.addEventStreamStatements(treeNode, baseUrl));
        }
        if (!treeNode.getMembers().isEmpty()) {
            treeNode.getMembers().stream()
                    .map(Member::model).forEach(model::add);
        }

        return prefixAdder.addPrefixesToModel(model);
    }
}
