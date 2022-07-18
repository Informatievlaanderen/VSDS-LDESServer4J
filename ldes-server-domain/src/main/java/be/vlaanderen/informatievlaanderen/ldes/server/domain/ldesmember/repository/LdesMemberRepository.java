package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface LdesMemberRepository {

    LdesMember saveLdesMember(LdesMember ldesMember, String memberType);

    List<LdesMember> fetchLdesMembers();

    Optional<LdesMember> getLdesMemberById(String id);

    Stream<LdesMember> getLdesMembersByIds(List<String> ids);
}
