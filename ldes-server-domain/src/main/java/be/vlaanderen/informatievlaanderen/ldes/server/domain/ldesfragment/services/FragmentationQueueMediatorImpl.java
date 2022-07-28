package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class FragmentationQueueMediatorImpl implements FragmentationQueueMediator{

    private final Logger logger = LoggerFactory.getLogger(FragmentationQueueMediatorImpl.class);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final LinkedBlockingQueue<String> ldesMembersToFragment = new LinkedBlockingQueue<>();
    private final FragmentationService fragmentationService;


    public FragmentationQueueMediatorImpl(FragmentationService fragmentationService) {
        this.fragmentationService = fragmentationService;
    }

    public void addLdesMember(String memberId){
        ldesMembersToFragment.add(memberId);
        executorService.submit(()->fragmentationService.addMemberToFragment(ldesMembersToFragment.poll()));
    }

    public boolean queueIsEmtpy(){
        return ldesMembersToFragment.isEmpty();
    }

    @Scheduled(fixedDelay = 60000)
    private void reportWaitingMembers() {
       logger.info("Number of Members queued for fragmentation:\t {}", ldesMembersToFragment.size());
    }
}
