package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FragmentationQueueMediatorImpl implements FragmentationQueueMediator {

	private final Logger logger = LoggerFactory.getLogger(FragmentationQueueMediatorImpl.class);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final LinkedBlockingQueue<String> ldesMembersToFragment = new LinkedBlockingQueue<>();
	private final FragmentationService fragmentationService;
	protected final AtomicInteger ldesMembersToFragmentTracker;

	public FragmentationQueueMediatorImpl(MeterRegistry meterRegistry,
			FragmentationService fragmentationService) {
		this.fragmentationService = fragmentationService;
		ldesMembersToFragmentTracker = meterRegistry.gauge("ldes_server_members_to_fragment", new AtomicInteger(0));
	}

	public void addLdesMember(String memberId) {
		ldesMembersToFragment.add(memberId);
		executorService.submit(() -> fragmentationService.addMemberToFragment(ldesMembersToFragment.poll()));
	}

	public boolean queueIsEmtpy() {
		return ldesMembersToFragment.isEmpty();
	}

	@Scheduled(fixedDelay = 1000)
	protected void reportWaitingMembers() {
		ldesMembersToFragmentTracker.set(ldesMembersToFragment.size());
		// TODO open discussion whether we also want to keep the logging together with
		// the metric exporter
		// logger.info("Number of Members queued for fragmentation:\t {}",
		// ldesMembersToFragment.size());
	}
}
