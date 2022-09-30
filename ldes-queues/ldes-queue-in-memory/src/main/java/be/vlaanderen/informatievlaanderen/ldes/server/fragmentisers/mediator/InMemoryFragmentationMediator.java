package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryFragmentationMediator implements FragmentationMediator {
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryFragmentationMediator.class);
	private final ExecutorService executorService;
	protected final LinkedBlockingQueue<LdesMember> ldesMembersToFragment = new LinkedBlockingQueue<>();

	private final FragmentationExecutor fragmentationExecutor;
	protected final AtomicInteger ldesMembersToFragmentTracker;

	public InMemoryFragmentationMediator(FragmentationExecutor fragmentationExecutor,
			MeterRegistry meterRegistry) {
		LOGGER.info("Server has been configured to queue ldes members for fragmentation IN MEMORY");
		this.fragmentationExecutor = fragmentationExecutor;
		this.executorService = Executors.newSingleThreadExecutor();
		ldesMembersToFragmentTracker = meterRegistry.gauge("ldes_server_members_to_fragment", new AtomicInteger(0));
	}

	@Override
	public void addMemberToFragment(LdesMember ldesMember) {
		ldesMembersToFragment.add(ldesMember);
		ldesMembersToFragmentTracker.set(ldesMembersToFragment.size());
		executorService.submit(() -> {
			fragmentationExecutor.executeFragmentation(ldesMembersToFragment.poll());
			ldesMembersToFragmentTracker.set(ldesMembersToFragment.size());
		});
	}
}
