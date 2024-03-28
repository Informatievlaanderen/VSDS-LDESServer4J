package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.memberingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.IngestValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.IngestValidator;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

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
        List<Resource> memberSubjects = getRootNode(model);
        if (memberSubjects.size() > 1 && !eventStream.isVersionCreationEnabled()) {
            throw new IngestValidationException("Only 1 member is allowed per ingest");
        }

        boolean conformsTimePath = validateTimestampPath(memberSubjects, model, eventStream);
        boolean conformsVersionPath = validateVersionOfPath(memberSubjects, model, eventStream);
        if (!(conformsTimePath && conformsVersionPath)) {
            String message = getValidationMessage(eventStream, conformsTimePath, conformsVersionPath);
            throw new IngestValidationException(message);
        }
    }

    private boolean validateTimestampPath(List<Resource> memberSubjects, Model model, EventStream eventStream) {
        List<Statement> timestampStatements = getStatementsOfPath(memberSubjects, model, eventStream.getTimestampPath());

        timestampStatements.forEach(statement -> {
            if (!statement.getObject().isLiteral() || !Objects.equals(statement.getObject().asLiteral().getDatatype(), XSDDatatype.XSDdateTime)) {
                throw new IngestValidationException(String.format("Object of statement with property: %s should be a literal of type %s", eventStream.getTimestampPath(), XSDDatatype.XSDdateTime.getURI()));
            }
        });

        return eventStream.isVersionCreationEnabled() ? timestampStatements.isEmpty() : timestampStatements.size() == 1;
    }

    private boolean validateVersionOfPath(List<Resource> memberSubjects, Model model, EventStream eventStream) {
        List<Statement> versionOfStatements = getStatementsOfPath(memberSubjects, model, eventStream.getVersionOfPath());

        versionOfStatements.forEach(statement -> {
            if (!statement.getObject().isResource()) {
                throw new IngestValidationException(String.format("Object of statement with property: %s should be a resource.", eventStream.getVersionOfPath()));
            }
        });
        return eventStream.isVersionCreationEnabled() ? versionOfStatements.isEmpty() : versionOfStatements.size() == 1;
    }

    private List<Statement> getStatementsOfPath(List<Resource> memberSubjects, Model model, String path) {
        return model.listStatements(null, ResourceFactory.createProperty(path), (RDFNode) null).filterKeep(statement -> memberSubjects.contains(statement.getSubject())).toList();
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

    private String getValidationMessage(EventStream eventStream, boolean conformsTimePath, boolean conformsVersionPath) {
        StringBuilder message = new StringBuilder("Member ingested on collection ")
                .append(eventStream.getCollection())
                .append(" should ");
        if (eventStream.isVersionCreationEnabled()) {
            message.append("not ");
        }
        message.append("contain ");
        if (!conformsTimePath) {
            message.append("the timestamp path: ")
                    .append(eventStream.getTimestampPath());
        }
        if (!(conformsTimePath || conformsVersionPath)) {
            message.append(" and ");
        }
        if (!conformsVersionPath) {
            message.append("the version of path: ")
                    .append(eventStream.getVersionOfPath());
        }
        if (!eventStream.isVersionCreationEnabled()) {
            message.append(" exactly once.");
        }
        return message.toString();
    }
}
