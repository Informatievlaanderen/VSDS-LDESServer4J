package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.ingestreportvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Order(2)
@Component
public class PathsValidator implements IngestReportValidator {

    @Override
    public void validate(Model model, EventStream eventStream, ShaclReportManager reportManager) {
        List<Resource> memberSubjects = model.listSubjects().filterDrop(RDFNode::isAnon).toList();

        validateTimestampPath(memberSubjects, model, eventStream, reportManager);
        validateVersionOfPath(memberSubjects, model, eventStream, reportManager);
    }

    private void validateTimestampPath(List<Resource> memberSubjects, Model model, EventStream eventStream, ShaclReportManager reportManager) {
        int expectedNumber = eventStream.isVersionCreationEnabled() ? 0 : 1;
        memberSubjects.forEach(subject -> {
            List<Statement> timestampStatements = getStatementsOfPath(subject, model, eventStream.getTimestampPath());
            if (timestampStatements.size() != expectedNumber) {
                reportManager.addEntry(subject,
                        String.format(String.format("Member must have exactly %s statement%s with timestamp path: %s as predicate.", expectedNumber, expectedNumber == 1 ? "" : "s", eventStream.getTimestampPath()))
                );
            }

            timestampStatements.forEach(statement -> {
                if (!statement.getObject().isLiteral() || !Objects.equals(statement.getObject().asLiteral().getDatatype(), XSDDatatype.XSDdateTime)) {
                    reportManager.addEntry(subject,
                            String.format(String.format("Object of statement with predicate: %s should be a literal of type %s", eventStream.getTimestampPath(), XSDDatatype.XSDdateTime.getURI()))
                    );
                }
            });
        });
    }

    private void validateVersionOfPath(List<Resource> memberSubjects, Model model, EventStream eventStream, ShaclReportManager reportManager) {
        int expectedNumber = eventStream.isVersionCreationEnabled() ? 0 : 1;
        memberSubjects.forEach(subject -> {
            List<Statement> versionOfStatements = getStatementsOfPath(subject, model, eventStream.getVersionOfPath());
            if (versionOfStatements.size() != expectedNumber) {
                reportManager.addEntry(subject,
                        String.format("Member must have exactly %s statement%s with versionOf path: %s as predicate.", expectedNumber, expectedNumber == 1 ? "" : "s", eventStream.getVersionOfPath())
                );
            }

            versionOfStatements.forEach(statement -> {
                if (!statement.getObject().isResource()) {
                    reportManager.addEntry(subject,
                            String.format("Object of statement with predicate: %s should be a resource", eventStream.getVersionOfPath())
                    );
                }
            });
        });
    }

    private List<Statement> getStatementsOfPath(Resource memberSubject, Model model, String path) {
        return model
                .listStatements(memberSubject, ResourceFactory.createProperty(path), (RDFNode) null)
                .filterDrop(statement -> statement.getSubject().isAnon())
                .toList();
    }
}
