package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamConverter;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@Observed
@RestController
@RequestMapping(value = "/admin/api/v1/eventstreams")
public class AdminEventStreamsRestController implements OpenApiAdminEventStreamsController {

    private static final Logger log = LoggerFactory.getLogger(AdminEventStreamsRestController.class);

    private final EventStreamService eventStreamService;
    private final EventStreamConverter eventStreamConverter;
    private final ModelValidator eventStreamValidator;

    public AdminEventStreamsRestController(EventStreamService eventStreamService,
                                           @Qualifier("eventStreamShaclValidator") ModelValidator eventStreamValidator,
                                           EventStreamConverter eventStreamConverter) {
        this.eventStreamService = eventStreamService;
        this.eventStreamValidator = eventStreamValidator;
        this.eventStreamConverter = eventStreamConverter;
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(eventStreamValidator);
    }

    @Override
    @GetMapping
    public List<EventStreamTO> getEventStreams() {
        return eventStreamService.retrieveAllEventStreams();
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @Override
    @PostMapping(consumes = {contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle})
    public EventStreamTO createEventStream(@RequestBody @Validated Model eventStreamModel) {
        EventStreamTO eventStreamTO = eventStreamConverter.fromModel(eventStreamModel);
        log.atInfo().log("START creating collection {}", eventStreamTO.getCollection());
        eventStreamService.createEventStream(eventStreamTO);
        log.atInfo().log("FINISHED creating collection {}", eventStreamTO.getCollection());
        return eventStreamTO;
    }

    @Override
    @GetMapping("/{collectionName}")
    public EventStreamTO getEventStream(@PathVariable String collectionName) {
        return eventStreamService.retrieveEventStream(collectionName);
    }

    @Override
    @DeleteMapping("/{collectionName}")
    public void deleteEventStream(@PathVariable String collectionName) {
        log.atInfo().log("START deleting collection {}", collectionName);
        eventStreamService.deleteEventStream(collectionName);
        log.atInfo().log("FINISHED deleting collection {}", collectionName);
    }

}
