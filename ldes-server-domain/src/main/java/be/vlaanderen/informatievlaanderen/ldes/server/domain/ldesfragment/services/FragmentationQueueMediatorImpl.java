package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FragmentationQueueMediatorImpl implements FragmentationQueueMediator {

	private final Logger logger = LoggerFactory.getLogger(FragmentationQueueMediatorImpl.class);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final LinkedBlockingQueue<String> ldesMembersToFragment = new LinkedBlockingQueue<>();
	private final Queue<FragmentationService> fragmentationServicesQueue;

	protected final AtomicInteger ldesMembersToFragmentTracker;

	public FragmentationQueueMediatorImpl(ListableBeanFactory beanFactory, LdesConfig ldesConfig,
			MeterRegistry meterRegistry) {
		Map<String, FragmentationService> availableServices = beanFactory.getBeansOfType(FragmentationService.class);
		this.fragmentationServicesQueue = configureFragmentationServices(ldesConfig.getFragmentations().keySet(),
				availableServices);
		ldesMembersToFragmentTracker = meterRegistry.gauge("ldes_server_members_to_fragment", new AtomicInteger(0));
	}

	public void addLdesMember(String memberId) {
		ldesMembersToFragment.add(memberId);
		executorService.submit(() -> addMemberToFragment(ldesMembersToFragment.poll()));
	}

	public boolean queueIsEmtpy() {
		return ldesMembersToFragment.isEmpty();
	}

	private Queue<FragmentationService> configureFragmentationServices(Set<String> fragmentations,
			Map<String, FragmentationService> availableServices) {
		Queue<FragmentationService> fragmentationServices = new ArrayDeque<>();
		fragmentations.forEach(s -> {
			fragmentationServices.add(availableServices.get(s));
		});
		return fragmentationServices;
	}

	private void addMemberToFragment(String memberId) {
		List<LdesFragment> ldesFragments = new ArrayList();

		for (FragmentationService fragmentationService : fragmentationServicesQueue) {
			ldesFragments = fragmentationService.addMemberToFragment(ldesFragments, memberId);
		}
	}

	@Scheduled(fixedDelay = 1000)
	protected void reportWaitingMembers() {
		ldesMembersToFragmentTracker.set(ldesMembersToFragment.size());
	}
}
