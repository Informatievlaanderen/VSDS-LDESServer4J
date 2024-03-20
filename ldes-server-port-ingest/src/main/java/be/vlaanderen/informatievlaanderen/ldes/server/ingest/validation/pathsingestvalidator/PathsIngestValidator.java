package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.pathsingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ModelIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ShaclModelValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

public class PathsIngestValidator implements MemberIngestValidator {
    private final Map<String, Boolean> receivesVersionObjectsMap = new HashMap<>();
    private final ModelIngestValidator versionValidator;
    private final ModelIngestValidator stateValidator;

    public PathsIngestValidator(ModelIngestValidator versionValidator, ModelIngestValidator stateValidator) {
        this.versionValidator = versionValidator;
        this.stateValidator = stateValidator;
    }


    @EventListener
    public void handleEventStreamInitEvent(EventStreamCreatedEvent event) {
        receivesVersionObjectsMap.put(event.eventStream().getCollection(), true /*Todo: change with version object implementation */);
    }

    @Override
    public void validate(Member member) {
        Boolean receivesVersionObjects = receivesVersionObjectsMap.get(member.getCollectionName());
        if (receivesVersionObjects == null) {
            throw new RuntimeException();
        } else if (receivesVersionObjects) {
            versionValidator.validate(member.getModel());
        } else {
            stateValidator.validate(member.getModel());
        }
    }
}
