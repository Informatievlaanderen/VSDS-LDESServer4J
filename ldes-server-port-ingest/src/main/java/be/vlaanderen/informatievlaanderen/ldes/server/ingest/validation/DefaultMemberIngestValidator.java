package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

public class DefaultMemberIngestValidator implements MemberIngestValidator {

    private final AppConfig appConfig;
    private final ShaclCollection shaclCollection;

    public DefaultMemberIngestValidator(AppConfig appConfig, ShaclCollection shaclCollection) {
        this.appConfig = appConfig;
        this.shaclCollection = shaclCollection;
    }

    @Override
    public void validate(Member member) {
        final String collectionName = member.getCollectionName();
        try {
            shaclCollection.retrieveShape(collectionName)
                    .ifPresent(shaclShape -> new LdesShaclValidator(shaclShape.getModel(), appConfig.getLdesConfig(collectionName)).validate(member));
        } catch (LdesShaclValidationException validationException) {
            throw new IngestValidationException(validationException.getMessage());
        }
    }

}
