package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.valueobjects.MemberModel;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberModelExtractor {
    private static class ModelInfo {
        public final List<Statement> statements = new ArrayList<>();
        public final List<Resource> references = new ArrayList<>();
        private boolean processed = false;
        public boolean isProcessed() {
            return processed;
        }
        public void markProcessed() {
            processed = true;
        }
    }

    private final Model model;
    private final Map<Resource, ModelInfo> statementsPerSubject = new HashMap<>();

    private MemberModelExtractor(Model model) {
        this.model = model;
    }

    private MemberModelExtractor splitStatementsPerSubject() {
        model.listStatements().forEach(statement -> {
            Resource subject = statement.getSubject();
            ModelInfo info = statementsPerSubject.computeIfAbsent(subject, x -> new ModelInfo());
            info.statements.add(statement);
            RDFNode object = statement.getObject();
            if (object.isAnon()) {
                info.references.add(object.asResource());
            }
        });
        return this;
    }

    public static MemberModelExtractor initialize(Model model) {
        return new MemberModelExtractor(model).splitStatementsPerSubject();
    }

    public List<MemberModel> extractAllMemberModels() {
        return statementsPerSubject.keySet().stream()
                .filter(RDFNode::isURIResource)
                .map(subject -> new MemberModel(subject.getURI(), extractMemberModel(subject)))
                .toList();
    }

    public Model extractMemberModel(Resource subject) {
        Model member = ModelFactory.createDefaultModel();
        deepCopyToModel(subject, member);
        return member;
    }

    private void deepCopyToModel(Resource subject, Model member) {
        ModelInfo info = statementsPerSubject.get(subject);
        if (info != null && !info.isProcessed()) {
            member.add(info.statements);
            info.markProcessed();
            info.references.forEach(x -> this.deepCopyToModel(x, member));
        }
    }
}
