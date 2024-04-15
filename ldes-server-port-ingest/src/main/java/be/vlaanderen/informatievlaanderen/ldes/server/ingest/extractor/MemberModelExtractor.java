package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.valueobjects.MemberModel;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.List;

public class MemberModelExtractor {
    private final Model model;
    private final List<Resource> processedSubjects = new ArrayList<>();

    private MemberModelExtractor(Model model) {
        this.model = model;
    }

    public static MemberModelExtractor initialize(Model model) {
        return new MemberModelExtractor(model);
    }

    public List<MemberModel> extractAllMemberModels() {
        return model.listSubjects()
                .filterDrop(RDFNode::isAnon)
                .mapWith(subject -> new MemberModel(subject.getURI(), extractMemberModel(subject)))
                .toList();
    }

    public Model extractMemberModel(Resource subject) {
        processedSubjects.add(subject);
        Model member = ModelFactory.createDefaultModel();
        model.listStatements().forEach(statement -> {
            if (statementBelongsToSubject(subject, statement)) {
                member.add(statement);
                if (statementContainsProcessableBNode(statement)) {
                    member.add(extractMemberModel(statement.getObject().asResource()));
                }
            }
        });
        return member;
    }

    private boolean statementBelongsToSubject(RDFNode subject, Statement statement) {
        return statement.getSubject().equals(subject);
    }

    private boolean statementContainsProcessableBNode(Statement statement) {
        return statement.getObject().isAnon() && !processedSubjects.contains(statement.getObject().asResource());
    }
}
