package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Member;

public interface MemberRepository {
	Member saveMember(Member member);
	void allocateMember(String memberId, ViewName viewName);

}
