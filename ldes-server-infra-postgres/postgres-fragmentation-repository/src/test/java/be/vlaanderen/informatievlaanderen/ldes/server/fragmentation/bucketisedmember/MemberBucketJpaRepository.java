package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.bucketisedmember;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberBucketJpaRepository extends JpaRepository<MemberBucketEntity, String> {
}
