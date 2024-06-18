package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.MemberBucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BucketisedMemberPostgresRepository implements BucketisedMemberRepository {
    private static final String COLLECTION_VIEW_SEPARATOR = "/";
    private final MemberBucketEntityRepository memberBucketEntityRepository;

    public BucketisedMemberPostgresRepository(MemberBucketEntityRepository memberBucketEntityRepository) {
        this.memberBucketEntityRepository = memberBucketEntityRepository;
    }

    @Override
    @Transactional
    public void deleteByViewName(ViewName viewName) {
        memberBucketEntityRepository.deleteAllByViewName(viewName.asString());
    }

    @Override
    @Transactional
    public void deleteByCollection(String collectionName) {
        memberBucketEntityRepository.deleteAllByViewNameStartingWith(collectionName + COLLECTION_VIEW_SEPARATOR);
    }
}
