package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

class PaginationServiceTest {
    private final ViewName VIEW_NAME_1 = new ViewName("collection", "view1");
    private final ViewName VIEW_NAME_2 = new ViewName("collection", "view2");
    private MemberPaginationService service1;
    private MemberPaginationService service2;
    private MemberPaginationServiceCreator memberPaginationServiceCreator;
    private ExecutorService executorService;
    private PaginationService paginationService;

    @BeforeEach
    void setUp() {
        memberPaginationServiceCreator = Mockito.mock(MemberPaginationServiceCreator.class);
        executorService = mock(ExecutorService.class);
        service1 = Mockito.mock(MemberPaginationService.class);
        service2 = Mockito.mock(MemberPaginationService.class);
        paginationService = new PaginationService(memberPaginationServiceCreator);
    }

    @Test
    void when_MemberBucketised_Then_CorrectServiceCalled() {
        when(memberPaginationServiceCreator.createPaginationService(eq(VIEW_NAME_1), any()))
                .thenReturn(service1);
        when(memberPaginationServiceCreator.createPaginationService(eq(VIEW_NAME_2), any()))
                .thenReturn(service2);
        paginationService.handleViewAddedEvent(new ViewAddedEvent(new ViewSpecification(VIEW_NAME_1,
                List.of(), List.of(), 10)));
        paginationService.handleViewAddedEvent(new ViewAddedEvent(new ViewSpecification(VIEW_NAME_2,
                List.of(), List.of(), 10)));
        when(service1.isRunning()).thenReturn(false);
        when(service2.isRunning()).thenReturn(false);
        when(executorService.submit(any(Callable.class))).thenReturn(new CompletableFuture<>());

        paginationService.handleMemberBucketisedEvent(new MemberBucketisedEvent(VIEW_NAME_1));

        InOrder inOrder = inOrder(executorService, service1, service2);
        inOrder.verify(service1).isRunning();
        inOrder.verify(service1).setTask(any());
        inOrder.verify(service1, times(1)).paginateMember();;
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void when_ViewDeleted_Then_ServiceRemoved() {
        when(memberPaginationServiceCreator.createPaginationService(eq(VIEW_NAME_1), any()))
                .thenReturn(service1);
        when(memberPaginationServiceCreator.createPaginationService(eq(VIEW_NAME_2), any()))
                .thenReturn(service2);
        paginationService.handleViewInitializationEvent(new ViewInitializationEvent(new ViewSpecification(VIEW_NAME_1,
                List.of(), List.of(), 10)));
        when(service1.isRunning()).thenReturn(true);

        paginationService.handleViewDeletedEvent(new ViewDeletedEvent(VIEW_NAME_1));

        InOrder inOrder = inOrder(service1, service2);
        inOrder.verify(service1).isRunning();
        inOrder.verify(service1).stopTask();
    }
}