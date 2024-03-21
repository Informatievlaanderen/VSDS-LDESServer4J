package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.pathsingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.IngestValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.IngestValidator;
import org.apache.jena.rdf.model.*;
import org.springframework.context.event.EventListener;

import java.util.*;

public class PathsIngestValidator implements IngestValidator {
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
        Set<Resource> namedSubjects = model.listStatements().mapWith(Statement::getSubject).filterDrop(RDFNode::isAnon).toSet();
        if (namedSubjects.size() != 1) {
            throw new IngestValidationException("Member must not contain named graphs");
        }
        Resource memberSubject = namedSubjects.iterator().next();
        boolean conformsTimePath = model.listStatements(memberSubject, ResourceFactory.createProperty(eventStream.getTimestampPath()), (RDFNode) null).toList().size() == 1 ^ eventStream.isVersionCreationEnabled();
        boolean conformsVersionPath = model.listStatements(memberSubject, ResourceFactory.createProperty(eventStream.getVersionOfPath()), (RDFNode) null).toList().size() == 1 ^ eventStream.isVersionCreationEnabled();
        if (!(conformsTimePath && conformsVersionPath)) {
            String message = getValidationMessage(eventStream, conformsTimePath, conformsVersionPath);
            throw new IngestValidationException(message);
        }
    }

    private String getValidationMessage(EventStream eventStream, boolean conformsTimePath, boolean conformsVersionPath) {
        StringBuilder message = new StringBuilder("Member ingested on collection ");
        message.append(eventStream.getCollection());
        message.append(" should ");
        if (eventStream.isVersionCreationEnabled()) {
            message.append("not ");
        }
        message.append("contain ");
        if (!conformsTimePath) {
            message.append("the timestamp path: ");
            message.append(eventStream.getTimestampPath());
        }
        if (!(conformsTimePath || conformsVersionPath)) {
            message.append(" and ");
        }
        if (!conformsVersionPath) {
            message.append("the version of path: ");
            message.append(eventStream.getVersionOfPath());
        }
        message.append(" exactly once.");
        return message.toString();
    }
}
