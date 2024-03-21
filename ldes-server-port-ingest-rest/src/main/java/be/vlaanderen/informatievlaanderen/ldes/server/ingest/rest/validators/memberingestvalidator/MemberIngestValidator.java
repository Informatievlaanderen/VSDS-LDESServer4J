package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.memberingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.IngestValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.IngestValidator;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.XML_DATETIME;

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
        List<Statement> timestampStatements = model.listStatements(null, ResourceFactory.createProperty(eventStream.getTimestampPath()), (RDFNode) null).filterKeep(statement -> memberSubjects.contains(statement.getSubject())).toList();
        List<Statement> versionOfStatements = model.listStatements(null, ResourceFactory.createProperty(eventStream.getVersionOfPath()), (RDFNode) null).filterKeep(statement -> memberSubjects.contains(statement.getSubject())).toList();

        timestampStatements.forEach(statement -> {
            if (!statement.getObject().isLiteral() || !Objects.equals(statement.getObject().asLiteral().getDatatype().getURI(), XML_DATETIME.getURI())) {
                throw new IngestValidationException(String.format("Object of statement with property: %s should be a literal of type %s", eventStream.getTimestampPath(), XML_DATETIME.getURI()));
            }
        });
        versionOfStatements.forEach(statement -> {
            if (!statement.getObject().isResource()) {
                throw new IngestValidationException(String.format("Object of statement with property: %s should be a resource.", eventStream.getVersionOfPath()));
            }
        });

        boolean conformsTimePath = eventStream.isVersionCreationEnabled() ? timestampStatements.isEmpty() : timestampStatements.size() == 1;
        boolean conformsVersionPath = eventStream.isVersionCreationEnabled() ? versionOfStatements.isEmpty() : versionOfStatements.size() == 1;
        if (!(conformsTimePath && conformsVersionPath)) {
            String message = getValidationMessage(eventStream, conformsTimePath, conformsVersionPath);
            throw new IngestValidationException(message);
        }
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
