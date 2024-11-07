package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Order(1)
@Component
public class BlankNodesValidator implements IngestReportValidator {

    private static class BlankNodeInfo {
        public BlankNodeInfo(Resource s) { this.subject = s; }
        public final Resource subject;
        public final List<Statement> statements = new ArrayList<>();
        public final List<Statement> references = new ArrayList<>();
        public int referenceCount() {
            return references.size();
        }
    }

    private Map<Resource, BlankNodeInfo> createBlankNodeReferenceCounts(Model model) {
        Map<Resource, BlankNodeInfo> infoPerBlankNode = new HashMap<>();
        model.listStatements().forEach(statement -> {
            Resource subject = statement.getSubject();
            if (subject.isAnon()) {
                Resource blankNode = subject.asResource();
                BlankNodeInfo info = infoPerBlankNode.computeIfAbsent(blankNode, BlankNodeInfo::new);
                info.statements.add(statement);
            }
            RDFNode object = statement.getObject();
            if (object.isAnon()) {
                Resource blankNode = object.asResource();
                BlankNodeInfo info = infoPerBlankNode.computeIfAbsent(blankNode, BlankNodeInfo::new);
                info.references.add(statement);
            }
        });
        return infoPerBlankNode;
    }

    @Override
    public void validate(Model model, EventStream eventStream, ShaclReportManager reportManager) {
        Map<Resource, BlankNodeInfo> blankNodeReferenceCounts = createBlankNodeReferenceCounts(model);

        validateDanglingBlankNodes(blankNodeReferenceCounts, reportManager);
        validateBlankNodeScope(blankNodeReferenceCounts, reportManager);
    }

    private void validateDanglingBlankNodes(Map<Resource, BlankNodeInfo> blankNodeReferenceCounts, ShaclReportManager reportManager) {
        blankNodeReferenceCounts.values()
                .stream()
                .filter(x -> x.referenceCount() == 0)
                .toList()
                .forEach(x -> reportManager.addEntry(x.subject, x.statements, "Object graphs don't allow blank nodes to occur outside of a named object."));
    }

    private void validateBlankNodeScope(Map<Resource, BlankNodeInfo> blankNodeReferenceCounts, ShaclReportManager reportManager) {
        blankNodeReferenceCounts.values()
                .stream()
                .filter(x -> x.referenceCount() > 1)
                .toList()
                .forEach(x -> reportManager.addEntry(x.subject, x.references, "Blank nodes must be scoped to one object."));
    }
}
