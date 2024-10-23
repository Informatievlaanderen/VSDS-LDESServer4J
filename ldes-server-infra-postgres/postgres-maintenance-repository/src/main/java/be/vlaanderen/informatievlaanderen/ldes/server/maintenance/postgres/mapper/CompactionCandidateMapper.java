package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.projection.CompactionCandidateProjection;

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
