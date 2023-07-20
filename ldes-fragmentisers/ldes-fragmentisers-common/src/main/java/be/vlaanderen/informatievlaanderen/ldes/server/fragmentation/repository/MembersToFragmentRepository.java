package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Optional;

public interface MembersToFragmentRepository {
	void create(List<ViewName> views, Model model, long sequenceNr, String memberId);

	Optional<Member> getNextMemberToFragment(ViewName viewName);

	void delete(ViewName viewName, Long sequenceNr);
}
