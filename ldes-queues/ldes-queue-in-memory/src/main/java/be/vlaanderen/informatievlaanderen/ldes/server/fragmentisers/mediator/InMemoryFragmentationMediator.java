package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryFragmentationMediator implements FragmentationMediator {
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryFragmentationMediator.class);
	private final ExecutorService executorService;
	private final FragmentationExecutor fragmentationExecutor;
	protected final AtomicInteger ldesMembersToFragmentTracker;

	public InMemoryFragmentationMediator(FragmentationExecutor fragmentationExecutor) {
		LOGGER.info("Server has been configured to queue ldes members for fragmentation IN MEMORY");
		this.fragmentationExecutor = fragmentationExecutor;
		this.executorService = Executors.newSingleThreadExecutor();
		ldesMembersToFragmentTracker = Metrics.gauge("ldes_server_members_to_fragment_total", new AtomicInteger(0));
	}

	@Override
	public void addMemberToFragment(Member member) {
		executorService.execute(() -> {
			ldesMembersToFragmentTracker.incrementAndGet();
			fragmentationExecutor.executeFragmentation(member);
			ldesMembersToFragmentTracker.decrementAndGet();
		});
	}
}
