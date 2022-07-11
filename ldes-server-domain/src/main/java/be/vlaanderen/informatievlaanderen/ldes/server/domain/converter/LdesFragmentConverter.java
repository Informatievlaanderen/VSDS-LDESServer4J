package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.RDF_SYNTAX_TYPE;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class LdesFragmentConverter {

    private final LdesMemberRepository ldesMemberRepository;

    public LdesFragmentConverter(LdesMemberRepository ldesMemberRepository) {
        this.ldesMemberRepository = ldesMemberRepository;
    }

    public Model toModel(final LdesFragment ldesFragment){
        Model model = ModelFactory.createDefaultModel();
        model.add(addFragmentMetadata(ldesFragment));

         ldesFragment.getMemberIds()
                 .stream()
                 .map(ldesMemberRepository::getLdesMemberById)
                 .map(LdesMember::getModel)
                 .forEach(model::add);

        return model;
    }

    private List<Statement> addFragmentMetadata(LdesFragment ldesFragment) {
        List<Statement> statements = new ArrayList<>();
        Resource viewId = createResource(ldesFragment.getFragmentInfo().getView());
        Resource currrentFragmentId = createResource(ldesFragment.getFragmentId());

        if (ldesFragment.getFragmentInfo().getShape() != null) {
            statements.add(createStatement(viewId, TREE_SHAPE, createResource(ldesFragment.getFragmentInfo().getShape())));
        }
        if (ldesFragment.getFragmentInfo().getValue() != null)
            statements.add(createStatement(viewId, TREE_VIEW, currrentFragmentId));
        statements
                .add(createStatement(viewId, LDES_VERSION_OF, createResource("http://purl.org/dc/terms/isVersionOf")));
        statements.add(createStatement(viewId, LDES_TIMESTAMP_PATH,
                createResource("http://www.w3.org/ns/prov#generatedAtTime")));
        statements.add(createStatement(viewId, RDF_SYNTAX_TYPE, createResource("https://w3id.org/ldes#EventStream")));
        ldesFragment.getRelations().forEach(treeRelation -> {
            Resource treeRelationNode = createResource();
            statements.add(createStatement(currrentFragmentId, TREE_RELATION, treeRelationNode));
            statements.add(createStatement(treeRelationNode, TREE_VALUE, createResource(treeRelation.getTreeValue())));
            statements.add(createStatement(treeRelationNode, TREE_PATH, createResource(treeRelation.getTreePath())));
            statements.add(createStatement(treeRelationNode, TREE_NODE, createResource(treeRelation.getTreeNode())));
            statements
                    .add(createStatement(treeRelationNode, RDF_SYNTAX_TYPE, createResource(treeRelation.getRelation())));
        });
        return statements;
    }
}
