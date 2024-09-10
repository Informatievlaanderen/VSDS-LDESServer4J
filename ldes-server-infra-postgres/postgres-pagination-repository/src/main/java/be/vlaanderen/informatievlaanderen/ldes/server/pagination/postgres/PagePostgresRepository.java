package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch.PaginationRowMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Repository
public class PagePostgresRepository implements PageRepository {
	private final JdbcTemplate jdbcTemplate;
	private final PageEntityRepository pageEntityRepository;

	public PagePostgresRepository(JdbcTemplate jdbcTemplate, PageEntityRepository pageEntityRepository) {
		this.jdbcTemplate = jdbcTemplate;
		this.pageEntityRepository = pageEntityRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page getOpenPage(long bucketId) {
		String sql = """
				select p.page_id, p.bucket_id, p.partial_url, v.page_size, COUNT(member_id) AS assigned_members
				from views v
				join page_members pm on pm.view_id = v.view_id
				join pages p on p.page_id = pm.page_id
				join bucket_lastpage blp on blp.bucket_id = pm.bucket_id AND blp.last_page_id = p.page_id
				where pm.bucket_id = ?
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
	public int createPage(Long bucketId, String partialUrl) {
		String sql = """
				INSERT INTO pages (bucket_id, expiration, partial_url)
				            VALUES (?, NULL, ?)
				            ON CONFLICT (partial_url) DO UPDATE SET bucket_id = pages.bucket_id
				   RETURNING page_id;
				""";

		return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Long.class, bucketId, partialUrl)).intValue();
	}

	@Override
	@Transactional
	public void setPageImmutable(long pageId) {
		pageEntityRepository.setPageImmutable(pageId);
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
