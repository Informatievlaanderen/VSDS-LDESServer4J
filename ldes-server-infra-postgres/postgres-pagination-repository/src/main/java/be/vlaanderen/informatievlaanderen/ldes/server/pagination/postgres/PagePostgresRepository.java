package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch.PaginationRowMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class PagePostgresRepository implements PageRepository {
	private final JdbcTemplate jdbcTemplate;
	private final PageEntityRepository pageEntityRepository;
	private final PageRelationRepository pageRelationRepository;

	public PagePostgresRepository(JdbcTemplate jdbcTemplate, PageEntityRepository pageEntityRepository, PageRelationRepository pageRelationRepository) {
		this.jdbcTemplate = jdbcTemplate;
		this.pageEntityRepository = pageEntityRepository;
		this.pageRelationRepository = pageRelationRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page getOpenPage(long bucketId) {
		String sql = """
				 select DISTINCT p.page_id, p.bucket_id, p.partial_url, v.page_size, COUNT(member_id) AS assigned_members
				 FROM pages p
				          LEFT JOIN page_members m ON p.page_id = m.page_id
				          JOIN buckets b ON p.bucket_id = b.bucket_id
				          JOIN views v ON v.view_id = b.view_id
				 where b.bucket_id = ? AND p.page_id NOT IN (SELECT from_page_id FROM page_relations)
				 group by p.page_id, v.page_size
				 order by page_id
				""";
		return jdbcTemplate.query(sql, new PaginationRowMapper(), bucketId)
				.stream()
				.findFirst()
				.orElseThrow();
	}

	@Override
	@Transactional
	public Page createNewPage(Page parentPage) {
		String partialUrl = parentPage.createChildPartialUrl().asString();
		PageEntity newPage = new PageEntity(parentPage.getBucketId(), partialUrl);

		pageEntityRepository.save(newPage);
		pageRelationRepository.insertGenericBucketRelation(parentPage.getId(), newPage.getId());
		pageEntityRepository.setPageImmutable(parentPage.getId());

		return new Page(newPage.getId(), parentPage.getBucketId(), partialUrl, parentPage.getPageSize());
	}

	@Override
	@Transactional
	public void setChildrenImmutableByBucketId(long bucketId) {
		pageEntityRepository.setAllChildrenImmutableByBucketId(bucketId);
	}

	@Override
	@Transactional
	public void markAllPagesImmutableByCollectionName(String collectionName) {
		pageEntityRepository.markAllPagesImmutableByCollectionName(collectionName);
	}

	@Override
	public Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage) {
		return pageEntityRepository.findCompactionCandidates(viewName.getCollectionName(), viewName.getViewName(), capacityPerPage)
				.stream().map(projection -> new CompactionCandidate(projection.getFragmentId(), projection.getSize(), projection.getToPage(),
						projection.getImmutable(), projection.getExpiration(), projection.getBucketId(), projection.getPartialUrl()));
	}

	@Override
	@Transactional
	public void deleteOutdatedFragments(LocalDateTime deleteTime) {
		pageEntityRepository.deleteByExpirationBefore(deleteTime);
	}

	@Override
	@Transactional
	public void setDeleteTime(List<Long> ids, LocalDateTime deleteTime) {
		pageEntityRepository.setDeleteTime(ids, deleteTime);
	}
}
