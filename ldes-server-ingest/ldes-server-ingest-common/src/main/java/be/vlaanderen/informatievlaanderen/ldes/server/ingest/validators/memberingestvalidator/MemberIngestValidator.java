package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.memberingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.IngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator.IngestReportValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator.ShaclReportManager;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MemberIngestValidator implements IngestValidator {
    private final Set<EventStream> eventstreams = new HashSet<>();
    private final Set<String> closedEventstreams = new HashSet<>();
    private final List<IngestReportValidator> validators;

    public MemberIngestValidator(List<IngestReportValidator> validators) {
        this.validators = validators;
    }

    private void addEventStream(EventStream eventStream) {
        eventstreams.add(eventStream);
    }

    @EventListener
    public void handleEventStreamInitEvent(EventStreamCreatedEvent event) {
        if (!event.eventStream().isClosed()) {
            addEventStream(event.eventStream());
        } else {
            closedEventstreams.add(event.eventStream().getCollection());
        }
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        eventstreams.removeIf(eventStream -> Objects.equals(eventStream.getCollection(), event.collectionName()));
        closedEventstreams.remove(event.collectionName());
    }

    @EventListener
    public void handleEventStreamClosedEvent(EventStreamClosedEvent event) {
        eventstreams.removeIf(eventStream -> Objects.equals(eventStream.getCollection(), event.collectionName()));
        closedEventstreams.add(event.collectionName());
    }

    @Override
    public void validate(Model model, String collectionName) {
        checkIfCollectionClosed(collectionName);

        Optional<EventStream> eventStream = eventstreams.stream()
                .filter(stream -> Objects.equals(stream.getCollection(), collectionName))
                .findFirst();
        eventStream.ifPresent(stream -> validateModel(model, stream));
    }

    private void checkIfCollectionClosed(String collectionName) {
        if (closedEventstreams.contains(collectionName))
            throw new IllegalArgumentException("collection %s is closed".formatted(collectionName));
    }

    private void validateModel(Model model, EventStream eventStream) {
        ShaclReportManager reportManager = new ShaclReportManager();

        validators.forEach(validator -> validator.validate(model, eventStream, reportManager));
        ValidationReport report = reportManager.createReport();

        if (!report.conforms()) {
            throw new ShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE), report.getModel());
        }
    }
}
