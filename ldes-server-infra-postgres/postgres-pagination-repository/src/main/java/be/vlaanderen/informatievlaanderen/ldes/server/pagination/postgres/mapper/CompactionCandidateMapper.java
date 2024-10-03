package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.CompactionCandidateProjection;

public class CompactionCandidateMapper {
	private CompactionCandidateMapper() {}

	public static CompactionCandidate fromProjection(CompactionCandidateProjection projection) {
		return new CompactionCandidate(
				projection.getFragmentId(),
				projection.getSize(),
				projection.getToPage(),
				projection.getBucketId(),
				projection.getPartialUrl());
	}
}
