package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PageDeletionTimeSetter {
	private final PageRepository pageRepository;
	private final Duration compactionDuration;

	public PageDeletionTimeSetter(PageRepository pageRepository, ServerConfig serverConfig) {
        this.pageRepository = pageRepository;
		this.compactionDuration = Duration.parse(serverConfig.getCompactionDuration());
	}

	public void setDeleteTimeOfFragment(List<Long> ids) {
        pageRepository.setDeleteTime(ids, LocalDateTime.now().plus(compactionDuration));
	}
}
