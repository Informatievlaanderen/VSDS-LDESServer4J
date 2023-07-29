package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("fragmentation_member_to_fragment")
public class MemberToFragmentEntity {

	@Id
	private final String id;

	@Indexed
	private final long sequenceNr;

	@Indexed
	private final String viewName;

	private final String memberModel;

	private final String memberId;

	public MemberToFragmentEntity(String id, long sequenceNr, String viewName, String memberModel, String memberId) {
		this.id = id;
		this.sequenceNr = sequenceNr;
		this.viewName = viewName;
		this.memberModel = memberModel;
		this.memberId = memberId;
	}

	public static MemberToFragmentEntity from(MembersToFragmentEntityId id, String memberModel, String memberId) {
		return new MemberToFragmentEntity(
				id.viewName.asString() + "/" + id.sequenceNr,
				id.sequenceNr,
				id.viewName.asString(),
				memberModel,
				memberId
		);
	}

	public record MembersToFragmentEntityId(ViewName viewName, Long sequenceNr) {
	}

	public String getMemberModel() {
		return memberModel;
	}

	public String getMemberId() {
		return memberId;
	}

	public long getSequenceNr() {
		return sequenceNr;
	}

//	public MembersToFragmentEntityId getId() {
//		return id;
//	}
}
