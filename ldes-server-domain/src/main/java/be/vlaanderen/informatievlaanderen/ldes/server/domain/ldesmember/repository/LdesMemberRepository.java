package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

import java.util.List;
import java.util.Optional;

public interface LdesMemberRepository {

    LdesMember saveLdesMember(LdesMember ldesMember);

    List<LdesMember> fetchLdesMembers();

    Optional<LdesMember> getLdesMemberById(String id);
}
