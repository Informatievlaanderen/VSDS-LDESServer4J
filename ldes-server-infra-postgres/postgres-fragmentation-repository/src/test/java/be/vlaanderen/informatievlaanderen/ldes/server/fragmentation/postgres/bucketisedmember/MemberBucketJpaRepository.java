package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.bucketisedmember;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberBucketJpaRepository extends JpaRepository<MemberBucketEntity, String> {
}
