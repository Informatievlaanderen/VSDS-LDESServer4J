package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.PageCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class PaginationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaginationService.class);
    private final MemberPaginationServiceCreator memberPaginationServiceCreator;
    private final Map<ViewName, MemberPaginationService> map = new HashMap<>();
    private final ExecutorService executorService;

    public PaginationService(MemberPaginationServiceCreator memberPaginationServiceCreator) {
        this.memberPaginationServiceCreator = memberPaginationServiceCreator;
        executorService = createExecutorService();
    }

    @EventListener
    public void handleViewInitializationEvent(ViewInitializationEvent event) {
        addToMap(event.getViewName());
    }

    @EventListener
    public void handleViewAddedEvent(ViewAddedEvent event) {
        addToMap(event.getViewName());
    }

    @EventListener
    public void handleViewDeletedEvent(ViewDeletedEvent event) {
        map.remove(event.getViewName());
    }

    @EventListener
    public void handleMemberBucketisedEvent(MemberBucketisedEvent event) {
        ViewName viewName = event.viewName();
        MemberPaginationService paginationService = map.get(viewName);
        if (paginationService == null) {
            LOGGER.warn("Missing view: {}", viewName.asString());
        } else if (!paginationService.isRunning()) {
            executorService.execute(paginationService::paginateMember);
        }
    }

    private ExecutorService createExecutorService() {
        return new ThreadPoolExecutor(0, 5,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(5, true), new ThreadPoolExecutor.DiscardPolicy());
    }

    private void addToMap(ViewName viewName) {
        map.put(viewName, memberPaginationServiceCreator.createPaginationService(viewName));
    }
}
