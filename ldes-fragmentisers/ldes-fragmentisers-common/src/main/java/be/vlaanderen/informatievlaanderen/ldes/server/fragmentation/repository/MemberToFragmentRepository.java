package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;

import java.util.List;
import java.util.Optional;

public interface MemberToFragmentRepository {

	void create(List<ViewName> views, Member member);

	Optional<Member> getNextMemberToFragment(ViewName viewName);

	void delete(ViewName viewName, Long sequenceNr);

}
