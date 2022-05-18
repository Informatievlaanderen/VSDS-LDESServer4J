package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;

public interface LdesMemberConverter {

    LdesMemberEntity toEntity(LdesMember ldesMember);

    LdesMember fromEntity(LdesMemberEntity ldesMemberEntity);
}
