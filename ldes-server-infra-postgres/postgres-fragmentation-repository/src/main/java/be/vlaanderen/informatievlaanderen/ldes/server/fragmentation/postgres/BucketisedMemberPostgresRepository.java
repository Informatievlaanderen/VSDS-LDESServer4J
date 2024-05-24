package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.MemberBucketisationEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketisationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BucketisedMemberPostgresRepository implements BucketisedMemberRepository {
    private final BucketisationRepository bucketisationRepository;
    private final MemberBucketisationEntityMapper mapper;

    public BucketisedMemberPostgresRepository(BucketisationRepository bucketisationRepository, MemberBucketisationEntityMapper mapper) {
        this.bucketisationRepository = bucketisationRepository;
        this.mapper = mapper;
    }

    @Override
    public void insertAll(List<BucketisedMember> members) {
        bucketisationRepository.saveAll(members.stream().map(mapper::toMemberBucketisationEntity).toList());
    }

    @Override
    public List<BucketisedMember> getFirstUnallocatedMember(ViewName viewName, Long sequenceNr) {
        return bucketisationRepository.findAllByViewNameAndSequenceNr(viewName.asString(), sequenceNr)
                .stream().map(mapper::toBucketisedMember).toList();
    }
}
