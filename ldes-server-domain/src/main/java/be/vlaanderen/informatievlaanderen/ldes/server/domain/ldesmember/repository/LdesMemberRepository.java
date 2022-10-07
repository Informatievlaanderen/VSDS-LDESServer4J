package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface LdesMemberRepository {

	LdesMember saveLdesMember(LdesMember ldesMember);

	List<LdesMember> fetchLdesMembers();
	// TODO: consider making this method return Stream<LdesMember>, to avoid memory
	// problems
	// In case of the underlying mongo repository, this might be achieved by using
	// its method
	// Page<T> findAll(Pageable pageable), then stream the elements of the pages

	Optional<LdesMember> getLdesMemberById(String id);

	Stream<LdesMember> getLdesMembersByIds(List<String> ids);
}
