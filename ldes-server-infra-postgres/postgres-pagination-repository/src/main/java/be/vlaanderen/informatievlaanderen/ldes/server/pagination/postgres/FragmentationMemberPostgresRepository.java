package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.FragmentationMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.MemberRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class FragmentationMemberPostgresRepository implements MemberRepository {
    private final FragmentationMemberEntityRepository fragmentationMemberEntityRepository;

    public FragmentationMemberPostgresRepository(FragmentationMemberEntityRepository fragmentationMemberEntityRepository) {
        this.fragmentationMemberEntityRepository = fragmentationMemberEntityRepository;
    }

    @Override
    @Transactional
    public void updateIsFragmented(boolean isFragmented, List<Long> memberIds) {
        fragmentationMemberEntityRepository.updateIsFragmented(isFragmented, memberIds);
    }
}
