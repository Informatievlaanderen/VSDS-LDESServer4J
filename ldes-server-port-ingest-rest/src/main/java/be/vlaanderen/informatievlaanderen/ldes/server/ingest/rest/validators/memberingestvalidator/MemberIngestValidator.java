package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.memberingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.IngestValidator;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.validation.ReportEntry;
import org.apache.jena.shacl.validation.Severity;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.SHACL_SOURCE_CONSTRAINT_COMPONENT;

@Component
@Order(2)
public class MemberIngestValidator implements IngestValidator {
    private final Set<EventStream> eventstreams = new HashSet<>();

    private void addEventStream(EventStream eventStream) {
        eventstreams.add(eventStream);
    }

    @EventListener
    public void handleEventStreamInitEvent(EventStreamCreatedEvent event) {
        addEventStream(event.eventStream());
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        eventstreams.removeIf(eventStream -> Objects.equals(eventStream.getCollection(), event.collectionName()));
    }

    @Override
    public void validate(Model model, String collectionName) {
        Optional<EventStream> eventStream = eventstreams.stream()
                .filter(stream -> Objects.equals(stream.getCollection(), collectionName))
                .findFirst();
        eventStream.ifPresent(stream -> validateModel(model, stream));
    }

    private void validateModel(Model model, EventStream eventStream) {
        List<ReportEntry> reportEntries = new ArrayList<>();
        List<Resource> memberSubjects = getRootNode(model);
        if (memberSubjects.size() > 1 && !eventStream.isVersionCreationEnabled()) {
            memberSubjects.forEach(subject -> addEntry(reportEntries, subject,
                        "Only 1 member is allowed per request on collection with version creation disabled"
                )
            );
        }

        validateTimestampPath(memberSubjects, model, eventStream, reportEntries);
        validateVersionOfPath(memberSubjects, model, eventStream, reportEntries);

        if (!reportEntries.isEmpty()) {
            ValidationReport.Builder builder = ValidationReport.create();
            reportEntries.forEach(builder::addReportEntry);
            ValidationReport report = builder.build();
            throw new ShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE), report.getModel());
        }
    }

    private void validateTimestampPath(List<Resource> memberSubjects, Model model, EventStream eventStream, List<ReportEntry> entries) {
        int expectedNumber = eventStream.isVersionCreationEnabled() ? 0 : 1;
        memberSubjects.forEach(subject -> {
            List<Statement> timestampStatements = getStatementsOfPath(subject, model, eventStream.getTimestampPath());
            if (timestampStatements.size() != expectedNumber) {
                addEntry(entries, subject,
                        String.format(String.format("Member must have exactly %s statement%s with timestamp path: %s as predicate.", expectedNumber, expectedNumber == 1 ? "" : "s", eventStream.getTimestampPath()))
                );
            }

            timestampStatements.forEach(statement -> {
                if (!statement.getObject().isLiteral() || !Objects.equals(statement.getObject().asLiteral().getDatatype(), XSDDatatype.XSDdateTime)) {
                    addEntry(entries, subject,
                            String.format(String.format("Object of statement with predicate: %s should be a literal of type %s", eventStream.getTimestampPath(), XSDDatatype.XSDdateTime.getURI()))
                    );
                }
            });
        });
    }

    private void validateVersionOfPath(List<Resource> memberSubjects, Model model, EventStream eventStream, List<ReportEntry> entries) {
        int expectedNumber = eventStream.isVersionCreationEnabled() ? 0 : 1;
        memberSubjects.forEach(subject -> {
            List<Statement> versionOfStatements = getStatementsOfPath(subject, model, eventStream.getVersionOfPath());
            if (versionOfStatements.size() != expectedNumber) {
                addEntry(entries, subject,
                        String.format("Member must have exactly %s statement%s with versionOf path: %s as predicate.", expectedNumber, expectedNumber == 1 ? "" : "s", eventStream.getVersionOfPath())
                );
            }

            versionOfStatements.forEach(statement -> {
                if (!statement.getObject().isResource()) {
                    addEntry(entries, subject,
                            String.format("Object of statement with predicate: %s should be a resource", eventStream.getVersionOfPath())
                    );
                }
            });
        });
    }

    private List<Statement> getStatementsOfPath(Resource memberSubject, Model model, String path) {
        return model.listStatements(memberSubject, ResourceFactory.createProperty(path), (RDFNode) null).toList();
    }

    private List<Resource> getRootNode(Model model) {
        String queryString = """
                SELECT DISTINCT ?s\s
                        WHERE {\s
                            ?s ?p ?o .\s
                            FILTER NOT EXISTS {
                                ?s_in ?p_in ?s .
                            }\s
                        }
                """ ;
        Query query = QueryFactory.create(queryString) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            List<Resource> rootNodes = new ArrayList<>();
            results.forEachRemaining(sol -> rootNodes.add(sol.getResource("s")));
            return rootNodes;
        }
    }

    private void addEntry(List<ReportEntry> entries, Resource focusNode, String message) {
        ReportEntry entry = ReportEntry.create().focusNode(focusNode.asNode())
                .severity(Severity.Violation)
                .sourceConstraintComponent(NodeFactory.createURI(SHACL_SOURCE_CONSTRAINT_COMPONENT))
                .message(message);
        entries.add(entry);
    }
}
