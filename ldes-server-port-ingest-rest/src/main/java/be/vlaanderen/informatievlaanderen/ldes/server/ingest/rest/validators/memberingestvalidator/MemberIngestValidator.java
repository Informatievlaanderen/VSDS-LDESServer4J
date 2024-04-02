package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.memberingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.IngestValidator;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.validation.ReportEntry;
import org.apache.jena.shacl.validation.Severity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.SHACL_SOURCE_CONSTRAINT_COMPONENT;

@Component
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
        Map<Integer, List<Resource>> numberOfReferences = getNumberOfNodeReferences(model);
        List<Resource> memberSubjects = model.listSubjects().filterDrop(RDFNode::isAnon).toList();

        validateDanglingBlankNodes(numberOfReferences.getOrDefault(0, List.of()), model, reportEntries);
        validateBlankNodeScope(numberOfReferences, model, reportEntries);

        if (memberSubjects.size() > 1 && !eventStream.isVersionCreationEnabled()) {
            // To be removed when bulk ingest is allowed when version creation is disabled
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

    private void validateDanglingBlankNodes(List<Resource> subjects, Model model, List<ReportEntry> reportEntries) {
        subjects.forEach(subject-> {
            if (subject.isAnon()) {
                addEntry(reportEntries, subject, model.listStatements(subject, null, (RDFNode) null).toList(),
                        "Object graphs don't allow blank nodes to occur outside of a named object.");
            }
        });
    }

    private void validateBlankNodeScope(Map<Integer, List<Resource>> numberOfReferences, Model model, List<ReportEntry> reportEntries) {
        numberOfReferences.forEach((amount, resourceList) -> {
            if (amount > 1) {
                resourceList.forEach(resource -> addEntry(reportEntries,
                        resource,model.listStatements(null, null, resource).toList() ,
                        "Blank nodes must be scoped to one object."));
            }
        });

    }
    private void validateBlankNodeScope2(Model model, List<Resource> subjects, List<ReportEntry> reportEntries) {
        Map<Resource, List<Statement>> blankNodes = model.listStatements().filterKeep(statement -> statement.getObject().isAnon()).toList().stream().collect(Collectors.groupingBy(Statement::getResource));
        blankNodes.forEach((r, list) -> {
            Set<Resource> end = new HashSet<>();
            List<Resource> visit = new ArrayList<>();
            do {
                List<Statement> newList = new ArrayList<>();
                list.stream().forEach(statement -> {
                    if (isEndNode(statement.getSubject(), subjects)) {
                        end.add(statement.getSubject());
                    } else if (visit.contains(statement.getSubject())) {
                        addEntry(reportEntries, r, "Looping references are not allowed.");
                    } else {
                        List<Statement> statements = getReferencingNodes(model, statement.getSubject());
                        newList.addAll(statements);
                        visit.addAll(statements.stream().map(Statement::getResource).toList());
                    }
                });
                list = newList;
            } while (!list.isEmpty());
            if (end.size() != 1) {
                addEntry(reportEntries, r, "Blank nodes must be scoped to one named object.");
            }
        });

    }

    private boolean isEndNode(Resource node, List<Resource> subjects) {
        return subjects.contains(node);
    }

    private List<Statement> getReferencingNodes(Model model, Resource resource) {
        return model.listStatements(null, null, resource).toList();
    }

    private Map<Integer, List<Resource>> getNumberOfNodeReferences(Model model) {
        return model.listSubjects().toList().stream().collect(Collectors.groupingBy(s -> model.listStatements(null, null, s).mapWith(Statement::getSubject).toSet().size()));
    }

    private void addEntry(List<ReportEntry> entries, Resource focusNode, String message) {
        ReportEntry entry = createEntry(focusNode, message);
        entries.add(entry);
    }

    private void addEntry(List<ReportEntry> entries, Resource focusNode, List<Statement> offendingStatements, String message) {
        ReportEntry entry = createEntry(focusNode, message);
        entry.value(NodeFactory.createLiteral(offendingStatements.toString()));
        entries.add(entry);
    }

    private ReportEntry createEntry(Resource focusNode, String message) {
        return ReportEntry.create().focusNode(focusNode.asNode())
                .severity(Severity.Violation)
                .sourceConstraintComponent(NodeFactory.createURI(SHACL_SOURCE_CONSTRAINT_COMPONENT))
                .message(message);
    }
}
