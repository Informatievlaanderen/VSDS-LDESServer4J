package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

public class DefaultMemberIngestValidator implements MemberIngestValidator {

    private final ModelValidatorCollection modelValidatorCollection;

    public DefaultMemberIngestValidator(ModelValidatorCollection modelValidatorCollection) {
        this.modelValidatorCollection = modelValidatorCollection;
    }

    @Override
    public void validate(Member member) {
        modelValidatorCollection.retrieveValidator(member.getCollectionName()).validate(member.getModel());
    }

}
