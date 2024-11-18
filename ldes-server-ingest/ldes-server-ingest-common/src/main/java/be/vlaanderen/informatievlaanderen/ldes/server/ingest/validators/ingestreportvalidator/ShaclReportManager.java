package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.validation.ReportEntry;
import org.apache.jena.shacl.validation.Severity;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.SHACL_SOURCE_CONSTRAINT_COMPONENT;

public class ShaclReportManager {
    private final List<ReportEntry> reportEntries = new ArrayList<>();

    public ValidationReport createReport() {
        ValidationReport.Builder builder = ValidationReport.create();
        reportEntries.forEach(builder::addReportEntry);
        return builder.build();
    }

    public void addEntry(Resource focusNode, String message) {
        ReportEntry entry = createEntry(focusNode, message);
        reportEntries.add(entry);
    }

    public void addEntry(Resource focusNode, List<Statement> offendingStatements, String message) {
        ReportEntry entry = createEntry(focusNode, message);
        entry.value(NodeFactory.createLiteral(offendingStatements.toString()));
        reportEntries.add(entry);
    }

    private ReportEntry createEntry(Resource focusNode, String message) {
        return ReportEntry.create().focusNode(focusNode.asNode())
                .severity(Severity.Violation)
                .sourceConstraintComponent(NodeFactory.createURI(SHACL_SOURCE_CONSTRAINT_COMPONENT))
                .message(message);
    }
}
