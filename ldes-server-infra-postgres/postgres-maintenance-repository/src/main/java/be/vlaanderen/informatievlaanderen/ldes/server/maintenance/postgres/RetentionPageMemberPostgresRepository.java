package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.repository.RetentionPageMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.repository.PageMemberRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RetentionPageMemberPostgresRepository implements PageMemberRepository {

	private final RetentionPageMemberEntityRepository entityRepository;

	public RetentionPageMemberPostgresRepository(RetentionPageMemberEntityRepository entityRepository) {
		this.entityRepository = entityRepository;
	}

	@Override
	@Transactional
	public void setPageMembersToNewPage(long newPageId, List<Long> pageIds) {
		entityRepository.setPageMembersToNewPage(newPageId, pageIds);
	}

	@Override
	@Modifying
	@Transactional
	public void deleteByViewNameAndMembersIds(ViewName viewName, List<Long> memberIds) {
		entityRepository.removePageMembers(viewName.getCollectionName(), viewName.getViewName(), memberIds);
	}
}
