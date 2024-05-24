package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketisationEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberBucketisationEntityMapper {

    public MemberBucketisationEntity toMemberBucketisationEntity(BucketisedMember member) {
        return new MemberBucketisationEntity(member.fragmentId() + "/" + member.memberId(), member.viewName().asString(), member.fragmentId(),
                member.memberId(), member.sequenceNr());
    }

    public BucketisedMember toBucketisedMember(MemberBucketisationEntity entity) {
        return new BucketisedMember(entity.getMemberId(), ViewName.fromString(entity.getViewName()),
                entity.getFragmentId(), entity.getSequenceNr());
    }
}
