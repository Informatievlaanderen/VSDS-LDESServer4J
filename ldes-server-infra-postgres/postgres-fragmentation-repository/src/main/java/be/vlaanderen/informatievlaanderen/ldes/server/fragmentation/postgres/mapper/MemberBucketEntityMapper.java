package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberBucketEntityMapper {

    public MemberBucketEntity toMemberBucketisationEntity(BucketisedMember member) {
        return new MemberBucketEntity(member.viewName().asString(), member.bucketDescriptor(),
                "member.memberId()", 0L);
    }

    public BucketisedMember toBucketisedMember(MemberBucketEntity entity) {
        return new BucketisedMember(entity.getId(), ViewName.fromString(entity.getViewName()),
                entity.getFragmentId());
    }
}
