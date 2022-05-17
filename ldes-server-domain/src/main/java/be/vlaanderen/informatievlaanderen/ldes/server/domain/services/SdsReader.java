package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;

public interface SdsReader {
    LdesMember storeLdesMember(final LdesMember ldesMember);
}
