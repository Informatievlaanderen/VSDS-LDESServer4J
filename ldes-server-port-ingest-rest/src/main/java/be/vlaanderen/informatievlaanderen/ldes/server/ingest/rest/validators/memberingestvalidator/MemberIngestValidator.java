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

@Component
@Order(2)
public class MemberIngestValidator implements IngestValidator {
    private final List<EventStream> eventstreams = new ArrayList<>();

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
        Resource memberSubject = getRootNode(model);
        int timestampPathCount = model.listStatements(memberSubject, ResourceFactory.createProperty(eventStream.getTimestampPath()), (RDFNode) null).toList().size();
        int versionOfPathCount = model.listStatements(memberSubject, ResourceFactory.createProperty(eventStream.getVersionOfPath()), (RDFNode) null).toList().size();
        boolean conformsTimePath = eventStream.isVersionCreationEnabled() ? timestampPathCount == 0 : timestampPathCount == 1;
        boolean conformsVersionPath = eventStream.isVersionCreationEnabled() ? versionOfPathCount == 0 : versionOfPathCount == 1;
        if (!(conformsTimePath && conformsVersionPath)) {
            String message = getValidationMessage(eventStream, conformsTimePath, conformsVersionPath);
            throw new IngestValidationException(message);
        }
    }

    private Resource getRootNode(Model model) {
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
            QuerySolution soln = results.nextSolution();
            if (results.hasNext()) {
                throw new IngestValidationException("Only 1 member is allowed per ingest");
            }
            return soln.getResource("s");
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
