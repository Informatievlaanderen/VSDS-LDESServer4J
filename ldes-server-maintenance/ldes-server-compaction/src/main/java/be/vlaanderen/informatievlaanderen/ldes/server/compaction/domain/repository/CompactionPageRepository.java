package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.time.LocalDateTime;
import java.util.List;

public interface CompactionPageRepository {
	List<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage);
	void deleteOutdatedFragments(LocalDateTime deleteTime);
	void setDeleteTime(List<Long> ids, LocalDateTime deleteTime);
}
