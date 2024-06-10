package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.MemberBucketEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.MemberBucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BucketisedMemberPostgresRepository implements BucketisedMemberRepository {
    private static final String COLLECTION_VIEW_SEPARATOR = "/";
    private final MemberBucketEntityRepository memberBucketEntityRepository;
    private final MemberBucketEntityMapper mapper;

    public BucketisedMemberPostgresRepository(MemberBucketEntityRepository memberBucketEntityRepository, MemberBucketEntityMapper mapper) {
        this.memberBucketEntityRepository = memberBucketEntityRepository;
        this.mapper = mapper;
    }

    @Override
    public void insertAll(List<BucketisedMember> members) {
//        memberBucketEntityRepository.saveAll(members.stream().map(mapper::toMemberBucketisationEntity).toList());
    }

    @Override
    public List<BucketisedMember> getFirstUnallocatedMember(ViewName viewName, Long sequenceNr) {
        return memberBucketEntityRepository.findAllByViewNameAndSequenceNr(viewName.asString(), sequenceNr)
                .stream().map(mapper::toBucketisedMember).toList();
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
