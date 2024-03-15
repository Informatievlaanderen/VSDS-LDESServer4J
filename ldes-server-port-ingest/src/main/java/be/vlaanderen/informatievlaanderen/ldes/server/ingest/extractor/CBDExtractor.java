package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.List;

public class CBDExtractor {
    private final Model model;
    private final List<RDFNode> namedSubjects;
    private final List<RDFNode> processedSubjects = new ArrayList<>();

    private CBDExtractor(Model model, List<RDFNode> namedSubjects) {
        this.model = model;
        this.namedSubjects = namedSubjects;
    }

    public static CBDExtractor initialize(Model model) {
        List<RDFNode> namedSubjects = extractAllNodesSubjects(model);
        return new CBDExtractor(model, namedSubjects);
    }

    public List<RDFNode> getNamedSubjects() {
        return namedSubjects;
    }

    public List<Model> extractAllMemberModels() {
        return namedSubjects.stream()
                .map(this::extractMemberModel)
                .toList();
    }

    public Model extractMemberModel(RDFNode subject) {
        processedSubjects.add(subject);
        Model member = ModelFactory.createDefaultModel();
        model.listStatements().forEach(statement -> {
            if (statementBelongsToSubject(subject, statement)) {
                member.add(statement);
                if (statementContainsProcessableBNode(statement)) {
                    member.add(extractMemberModel(statement.getObject()));
                }
            }
        });
        return member;
    }

    private static List<RDFNode> extractAllNodesSubjects(Model model) {
        final List<RDFNode> namedNodes = new ArrayList<>();
        model.listSubjects().toList()
                .stream()
                .filter(subject -> !subject.isAnon())
                .forEach(namedNodes::add);
        return namedNodes;
    }

    private boolean statementBelongsToSubject(RDFNode subject, Statement statement) {
        return statement.getSubject().equals(subject);
    }

    private boolean statementContainsProcessableBNode(Statement statement) {
        return !statement.getObject().isLiteral()
                && !processedSubjects.contains(statement.getObject())
                && !namedSubjects.contains(statement.getObject());
    }
}
