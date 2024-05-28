package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberBucketEntityMapper {

    public MemberBucketEntity toMemberBucketisationEntity(BucketisedMember member) {
        return new MemberBucketEntity(member.viewName().asString(), member.fragmentId(),
                member.memberId(), member.sequenceNr());
    }

    public BucketisedMember toBucketisedMember(MemberBucketEntity entity) {
        return new BucketisedMember(entity.getMemberId(), ViewName.fromString(entity.getViewName()),
                entity.getFragmentId(), entity.getSequenceNr());
    }
}
