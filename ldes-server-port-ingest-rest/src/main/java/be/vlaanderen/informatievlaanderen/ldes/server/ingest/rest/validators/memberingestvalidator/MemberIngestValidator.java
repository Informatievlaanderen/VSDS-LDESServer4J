package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.memberingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.ingestreportvalidator.IngestReportValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.ingestreportvalidator.ShaclReportManager;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.IngestValidator;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MemberIngestValidator implements IngestValidator {
    private final Set<EventStream> eventstreams = new HashSet<>();
    private final List<IngestReportValidator> validators;

    public MemberIngestValidator(List<IngestReportValidator> validators) {
        this.validators = validators;
    }

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
        ShaclReportManager reportManager = new ShaclReportManager();

        validators.forEach(validator -> validator.validate(model, eventStream, reportManager));
        ValidationReport report = reportManager.createReport();

        if (!report.conforms()) {
            throw new ShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE), report.getModel());
        }
    }
}
