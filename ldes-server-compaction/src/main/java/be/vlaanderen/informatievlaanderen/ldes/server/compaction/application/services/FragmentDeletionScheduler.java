package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.PagePostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.spi.RetentionPolicyEmptinessChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.DELETION_CRON_KEY;

@Service
public class FragmentDeletionScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentDeletionScheduler.class);
	private static final Long DELETION_GRACE_PERIOD_DAYS = 7L;
	private final PagePostgresRepository pageRepository;
	private final RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker;

	public FragmentDeletionScheduler(PagePostgresRepository pageRepository, RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker) {
		this.pageRepository = pageRepository;
		this.retentionPolicyEmptinessChecker = retentionPolicyEmptinessChecker;
	}

	@SuppressWarnings("java:S6857")
	@Scheduled(cron = DELETION_CRON_KEY)
	public void deleteFragments() {
		if(retentionPolicyEmptinessChecker.isEmpty()) {
			LOGGER.atDebug().log("Fragment deletion skipped: no retention policies found.");
			return;
		}
		pageRepository.deleteOutdatedFragments(LocalDateTime.now().plusDays(DELETION_GRACE_PERIOD_DAYS));
	}
}
