package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.PageListSortException;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import org.apache.jena.rdf.model.EmptyListException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CompactedFragmentCreator {
	public static final String PAGE_NUMBER_REGEX = "pageNumber=.*";
	public static final String INSERT_COMPACTED_PAGE_SQL = "INSERT INTO pages (bucket_id, expiration, partial_url, immutable) VALUES (?, NULL, ?, true)";
	public static final String INSERT_PAGE_RELATION_SQL = "INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, ?)";
	private final JdbcTemplate jdbcTemplate;

	public CompactedFragmentCreator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

	public Long createCompactedPage(Collection<CompactionCandidate> pages) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		final CompactionCandidate lastPage = pages.stream()
				.filter(p -> pages.stream()
						.filter(p2 -> p2.getId() == p.getNextPageId()).findFirst().isEmpty())
				.findFirst().orElseThrow(() -> new PageListSortException(pages.stream().map(p -> String.valueOf(p.getId())).toList()));

		final String compactedPagePartialUrl = createCompactedPartialUrl(lastPage);
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(INSERT_COMPACTED_PAGE_SQL, new String[] {"page_id"});
			ps.setLong(1, lastPage.getBucketId());
			ps.setString(2, compactedPagePartialUrl);
			return ps;
		}, keyHolder);

		jdbcTemplate.update(INSERT_PAGE_RELATION_SQL, keyHolder.getKey(), lastPage.getNextPageId(), RdfConstants.GENERIC_TREE_RELATION);

		return keyHolder.getKeyAs(Long.class);
	}

	private String createCompactedPartialUrl(CompactionCandidate candidate) {
		Matcher matcher = Pattern.compile(PAGE_NUMBER_REGEX).matcher(candidate.getPartialUrl());
		return matcher.replaceFirst("pageNumber=" + UUID.randomUUID());
	}
}
