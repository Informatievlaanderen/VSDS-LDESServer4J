package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("fragmentation_member_to_fragment")
public class MemberToFragmentEntity {

	@Id
	private final MembersToFragmentEntityId id;

	private final String memberModel;

	private final String memberId;

	public MemberToFragmentEntity(MembersToFragmentEntityId id, String memberModel, String memberId) {
		this.id = id;
		this.memberModel = memberModel;
		this.memberId = memberId;
	}

	public record MembersToFragmentEntityId(ViewName viewName, Long sequenceNr) {
	}

	public String getMemberModel() {
		return memberModel;
	}

	public String getMemberId() {
		return memberId;
	}

	public MembersToFragmentEntityId getId() {
		return id;
	}
}
