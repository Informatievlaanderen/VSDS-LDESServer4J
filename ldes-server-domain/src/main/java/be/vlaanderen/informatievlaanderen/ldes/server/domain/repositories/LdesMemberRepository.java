package be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;

import java.util.List;

public interface LdesMemberRepository {

    LdesMember saveLdesMember(LdesMember ldesMember);

    List<LdesMember> fetchLdesMembers();
}
