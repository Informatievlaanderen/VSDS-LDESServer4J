package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FragmentationQueueMediatorImpl {

//	//	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
//	protected final LinkedBlockingQueue<String> ldesMembersToFragment = new LinkedBlockingQueue<>();
//	private final FragmentationExecutor fragmentationExecutor;
//
//	protected final AtomicInteger ldesMembersToFragmentTracker;
//
//	public FragmentationQueueMediatorImpl(MeterRegistry meterRegistry,
//			FragmentationExecutor fragmentationExecutor) {
//		this.fragmentationExecutor = fragmentationExecutor;
//		ldesMembersToFragmentTracker = meterRegistry.gauge("ldes_server_members_to_fragment", new AtomicInteger(0));
//	}
//
//	public void addLdesMember(String memberId) {
//		ldesMembersToFragment.add(memberId);
//		fragmentationExecutor.executeFragmentation(ldesMembersToFragment.poll());
//	}
//
//	public boolean queueIsEmtpy() {
//		return ldesMembersToFragment.isEmpty();
//	}
//
//	@Scheduled(fixedDelay = 1000)
//	protected void reportWaitingMembers() {
//		ldesMembersToFragmentTracker.set(ldesMembersToFragment.size());
//	}
}
